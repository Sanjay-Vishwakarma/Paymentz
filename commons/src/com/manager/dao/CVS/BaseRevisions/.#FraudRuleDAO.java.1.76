package com.manager.dao;

import com.directi.pg.*;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudRuleDAO
{
    Logger logger = new Logger(FraudRuleDAO.class.getName());
    TransactionLogger transactionLogger = new TransactionLogger(FraudRuleDAO.class.getName());
    static Functions functions = new Functions();

    public List<RuleMasterVO> getSuBAccountLevelRiskRuleList(String fsSubAccountId, PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT sarm.ruleid,rm.rulename,sarm.score,sarm.status FROM subaccount_rule_mapping AS sarm JOIN \n").append("rule_master AS rm ON sarm.ruleid=rm.ruleid AND sarm.fssubaccountid=? WHERE rm.rulegroup='Dynamic' order by sarm.ruleid desc limit ?,? " );

            StringBuilder countQry = new StringBuilder("SELECT count(sarm.ruleid) FROM subaccount_rule_mapping AS sarm JOIN \n").append("rule_master AS rm ON sarm.ruleid=rm.ruleid AND sarm.fssubaccountid=? WHERE rm.rulegroup='Dynamic' order by sarm.ruleid desc");

            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, fsSubAccountId);
            pstmt.setInt(2, paginationVO.getStart());
            pstmt.setInt(3, paginationVO.getEnd());
            logger.debug("Level pstmt::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setRuleDescription(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }

            pstmt = con.prepareStatement(countQry.toString());
            pstmt.setString(1, fsSubAccountId);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting sub account level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSuBAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting sub account level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSuBAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;

    }


    public List<RuleMasterVO> getSuBAccountLevelRiskRuleList(String fsSubAccountId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT sarm.ruleid,rm.rulename,sarm.score,sarm.status FROM subaccount_rule_mapping AS sarm JOIN \n" +
                    "rule_master AS rm ON sarm.ruleid=rm.ruleid AND sarm.fssubaccountid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fsSubAccountId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setRuleDescription(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting sub account level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSuBAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting sub account level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSuBAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return ruleMasterVOList;

    }
    public List<RuleMasterVO> getAccountLevelRiskRuleList(String fsAccountId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT arm.ruleid,rm.rulename,arm.score,arm.status FROM account_rule_mapping AS arm JOIN rule_master rm ON \n").append("arm.ruleid=rm.ruleid AND arm.fsaccountid=? WHERE rm.rulegroup IN ('HardCoded','Other')");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fsAccountId);
            logger.debug("account pstmt::::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setRuleDescription(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting account level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting account level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }

    public List<RuleMasterVO> getAccountLevelRiskRuleList(String fsAccountId,PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        PreparedStatement pstmtCount = null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT arm.ruleid,rm.rulename,arm.score,arm.status FROM account_rule_mapping AS arm JOIN rule_master rm ON \n" +
                    "arm.ruleid=rm.ruleid AND arm.fsaccountid=? order by arm.ruleid desc limit ?,? ");

            StringBuffer countQry =new StringBuffer("SELECT count(arm.ruleid) FROM account_rule_mapping AS arm JOIN rule_master As rm ON \n" +
                    "arm.ruleid=rm.ruleid AND arm.fsaccountid=? order by arm.ruleid desc");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fsAccountId);
            pstmt.setInt(2,paginationVO.getStart());
            pstmt.setInt(3, paginationVO.getEnd());
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setRuleDescription(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }

            pstmt=con.prepareStatement(countQry.toString());
            pstmt.setString(1,fsAccountId);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting account level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting account level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }

    public FraudSystemSubAccountVO getFraudServiceSubAccountDetails(String subAccountId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        FraudSystemSubAccountVO subAccountVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT fssubaccountid,fsaccountid,subaccountname,subusername,subpwd,isactive,FROM_UNIXTIME(dtstamp) AS creationon,").append("TIMESTAMP AS lastupdated, submerchantUsername,submerchantPassword FROM fsaccount_subaccount_mapping WHERE fssubaccountid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,subAccountId);
            logger.debug("rule pstmt:::::" + pstmt);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                subAccountVO=new FraudSystemSubAccountVO();
                subAccountVO.setFraudSystemSubAccountId(rs.getString("fssubaccountid"));
                subAccountVO.setFraudSystemAccountId(rs.getString("fsaccountid"));
                subAccountVO.setSubAccountName(rs.getString("subaccountname"));
                subAccountVO.setUserName(rs.getString("subpwd"));
                subAccountVO.setIsActive(rs.getString("creationon"));
                subAccountVO.setLastUpdated(rs.getString("lastupdated"));
                subAccountVO.setSubmerchantUsername(rs.getString("submerchantUsername"));
                subAccountVO.setSubmerchantPassword(rs.getString("submerchantPassword"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting fraud sub account details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudServiceSubAccountDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting fraud account details::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudServiceSubAccountDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return subAccountVO;
    }
    public String updateMemberSubAccountRuleMapping(String score,String status,String ruleId,String fsSubAccountId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("update subaccount_rule_mapping set score=?,status=? where fssubaccountid=? and ruleid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,score);
            pstmt.setString(2,status);
            pstmt.setString(3,fsSubAccountId);
            pstmt.setString(4,ruleId);
            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while updating merchant risk rule",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateMemberSubAccountRuleMapping()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while updating merchant risk rule::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateMemberSubAccountRuleMapping()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public String addNewFraudAccountRuleMap(FraudSystemAccountRuleVO fraudSystemAccountRuleVO)throws  PZDBViolationException
    {

        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into account_rule_mapping(fsaccountid,ruleid,score,status,value)" +
                    "values(?,?,?,?,?)");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, fraudSystemAccountRuleVO.getFsAccountId());
            pstmt.setString(2,fraudSystemAccountRuleVO.getRuleId());
            pstmt.setString(3,fraudSystemAccountRuleVO.getScore());
            pstmt.setString(4,fraudSystemAccountRuleVO.getStatus());
            if(functions.isValueNull(fraudSystemAccountRuleVO.getValue()))
            {
                pstmt.setString(5,fraudSystemAccountRuleVO.getValue());
            }
            else if(functions.isValueNull(fraudSystemAccountRuleVO.getValue1()))
            {
                pstmt.setString(5, fraudSystemAccountRuleVO.getValue1());
            }
            else if(functions.isValueNull(fraudSystemAccountRuleVO.getValue2()))
            {
                pstmt.setString(5, fraudSystemAccountRuleVO.getValue2());
            }
            else{
                pstmt.setNull(5,Types.VARCHAR);
            }
            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while Adding New Fraud Account Rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudAccountRuleMap()", null, "Common", "Sql exception while connecting to account_rule_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while Adding New Fraud Account Rule Mapping:",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudAccountRuleMap()", null, "Common", "Sql exception while connecting to account_rule_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        logger.debug("queryStatus in addNewFraudAccountRuleMap()===>"+queryStatus);
        return queryStatus;
    }

    public String  addNewFraudSubAccountRuleMap(FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO)throws  PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into subaccount_rule_mapping(fssubaccountid,ruleid,score,status,value)" +
                    "values(?,?,?,?,?)");

            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fraudSystemSubAccountRuleVO.getFsSubAccountId());
            pstmt.setString(2,fraudSystemSubAccountRuleVO.getRuleId());
            pstmt.setString(3,fraudSystemSubAccountRuleVO.getScore());
            pstmt.setString(4,fraudSystemSubAccountRuleVO.getStatus());
            if(functions.isValueNull(fraudSystemSubAccountRuleVO.getValue()))
            {
                pstmt.setString(5, fraudSystemSubAccountRuleVO.getValue());
            }
            else if(functions.isValueNull(fraudSystemSubAccountRuleVO.getValue1()))
            {
                pstmt.setString(5, fraudSystemSubAccountRuleVO.getValue1());
            }
            else if(functions.isValueNull(fraudSystemSubAccountRuleVO.getValue2()))
            {
                pstmt.setString(5, fraudSystemSubAccountRuleVO.getValue2());
            }
            else
            {
                pstmt.setNull(5, Types.VARCHAR);
            }
            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while Adding New Fraud Account Rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSubAccountRuleMap()", null, "Common", "Sql exception while connecting to subaccount_rule_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while Adding New Fraud Account Rule Mapping:",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSubAccountRuleMap()", null, "Common", "Sql exception while connecting to subaccount_rule_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        logger.debug("queryStatus in addNewFraudSubAccountRuleMap()===>"+queryStatus);
        return queryStatus;

    }

    public String addNewFraudRule(RuleMasterVO ruleMasterVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query = new StringBuffer("insert into rule_master(ruleid,rulename,ruledescription,rulegroup,score,status,dtstamp)" +
                    "values(null,?,?,?,?,?,unix_timestamp(now()))");
            pstmt = con.prepareStatement(query.toString());
//            pstmt.setString(1,ruleMasterVO.getRuleId());
            pstmt.setString(1, ruleMasterVO.getRuleName());
            pstmt.setString(2, ruleMasterVO.getRuleDescription());
            pstmt.setString(3, ruleMasterVO.getRuleGroup());
            pstmt.setString(4, ruleMasterVO.getDefaultScore());
            pstmt.setString(5, ruleMasterVO.getDefaultStatus());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                queryStatus = "success";
            }

        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while adding new rule ",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudRule()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while  adding new rule::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudRule()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        logger.debug("queryStatus===>"+queryStatus);
        return queryStatus;
    }
    public String addNewFraudSystemAccount(FraudSystemAccountVO  accountVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into fraudsystem_account_mapping(fsaccountid,accountname,username,password,fsid,isTest,contact_name,contact_email,dtstamp)" +
                    "values(null,?,?,?,?,?,?,?,unix_timestamp(now()))");

            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,accountVO.getAccountName());
            pstmt.setString(2,accountVO.getUserName());
            pstmt.setString(3,accountVO.getPassword());
            pstmt.setString(4,accountVO.getFraudSystemId());
            pstmt.setString(5,accountVO.getIsTest());
            pstmt.setString(6,accountVO.getContactName());
            pstmt.setString(7,accountVO.getContactEmail());

            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while creating fraud system account",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemAccount()", null, "Common", "Sql exception while connecting to fsaccount table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while creating fraud system account::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemAccount()", null, "Common", "Sql exception while connecting to fsaccount's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public String allocateNewFraudSystemAccount(String partnerId, String fsAccountId, String isActive)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into partner_fsaccounts_mapping(id,partnerid,fsaccountid,isActive,dtstamp)" +
                    "values(null,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,partnerId);
            pstmt.setString(2,fsAccountId);
            pstmt.setString(3,isActive);
            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQLException::::::::::",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "allocateNewFraudSystemAccount()", null, "Common", "Sql exception while connecting to partner_fsaccounts_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("SystemError:::::::::::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "allocateNewFraudSystemAccount()", null, "Common", "Sql exception while connecting to partner_fsaccounts_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public String DeleteFraudSystemAccount (String partnerid,String fsaccountid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        String queryStatus = "fail";
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("delete from partner_fsaccounts_mapping  where partnerid=? and fsaccountid=? ");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, partnerid);
            pstmt.setString(2, fsaccountid);
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                queryStatus = "delete";
            }

        }
        catch (Exception e)
        {
            logger.error("Exception--->",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public String addNewFraudSystemSubAccount(FraudSystemSubAccountVO  subAccountVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into fsaccount_subaccount_mapping(fssubaccountid,fsaccountid,subaccountname,subusername,subpwd,isactive,dtstamp)" +
                    "values(null,?,?,?,?,?,unix_timestamp(now()))");

            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,subAccountVO.getFraudSystemAccountId());
            pstmt.setString(2,subAccountVO.getSubAccountName());
            pstmt.setString(3,subAccountVO.getUserName());
            pstmt.setString(4,subAccountVO.getPassword());
            pstmt.setString(5,subAccountVO.getIsActive());

            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while creating fraud system sub-account",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemSubAccount()", null, "Common", "Sql exception while connecting to fsaccount table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while creating fraud system sub-account::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemSubAccount()", null, "Common", "Sql exception while connecting to fsaccount's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public String addNewMerchantFraudAccount(MerchantFraudAccountVO fraudAccountVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into merchant_fssubaccount_mappping(merchantfraudserviceid,memberid,fssubaccountid,isactive,isvisible,dtstamp)" +
                    "values(null,?,?,?,?,unix_timestamp(now()))");

            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fraudAccountVO.getMemberId());
            pstmt.setString(2,fraudAccountVO.getFsSubAccountId());
            pstmt.setString(3,fraudAccountVO.getIsActive());
            pstmt.setString(4,fraudAccountVO.getIsVisible());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                queryStatus="success";
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while creating Merchant fraud system Account",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewMerchantFraudAccount()", null, "Common", "Sql exception while connecting to fsaccount table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while creating fraud system sub-account::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewMerchantFraudAccount()", null, "Common", "Sql exception while connecting to fsaccount's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public boolean isFraudRuleUnique(String ruleName)throws PZDBViolationException
    {
        Connection con = null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select ruleid from rule_master where rulename=?");
            PreparedStatement ps = con.prepareStatement(query.toString());
            ps.setString(1,ruleName);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new rule ",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudRuleUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new rule::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudRuleUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isFraudAccountRuleMapUnique(String fsaccountid, String ruleId)throws  PZDBViolationException
    {
        Connection con = null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select accountruleid from account_rule_mapping where fsaccountid=? and ruleid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,fsaccountid);
            ps.setString(2, ruleId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }


    public boolean isFraudSubAccountRuleMapUnique(String fsSubAccountId, String ruleid) throws PZDBViolationException
    {
        Connection con = null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select subaccountruleid from subaccount_rule_mapping where fssubaccountid=? and ruleid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,fsSubAccountId);
            ps.setString(2,ruleid);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isFraudSystemAccountUnique(String accountname) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fsaccountid from fraudsystem_account_mapping where accountname=?");

            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,accountname);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while checking fraud system account availability",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemAccountUnique()", null, "Common", "Sql exception while connecting to fraudsystem_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Account Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemAccountUnique()", null, "Common", "Sql exception while connecting fraudsystem_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isAlreadyAllocated(String partnerId, String accountName) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select id from partner_fsaccounts_mapping where partnerid=? and fsaccountid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,partnerId);
            ps.setString(2,accountName);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQLException::::::::::::::::::::::",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isAlreadyAllocated()", null, "Common", "Sql exception while connecting to fraudsystem_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("SystemError:::::::::::::::::::::::::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isAlreadyAllocated()", null, "Common", "Sql exception while connecting fraudsystem_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isFraudSystemSubAccountUnique(String fsaccountid, String subaccountname) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fssubaccountid from fsaccount_subaccount_mapping where fsaccountid=? and subaccountname=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,fsaccountid);
            ps.setString(2,subaccountname);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Sub-account Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemSubAccountUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Sub-account Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemSubAccountUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean isFraudSystemSubAccountUnique(String subaccountname) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fssubaccountid from fsaccount_subaccount_mapping where subaccountname=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,subaccountname);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while checking merchant sub account ",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemSubAccountUnique()", null, "Common", "Sql exception while connecting to fsaccount_subaccount_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while checking merchant sub account ",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSystemSubAccountUnique()", null, "Common", "Sql exception while connecting to fsaccount_subaccount_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isMerchantFraudAccountUnique(String memberid, String fssubaccountid) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select merchantfraudserviceid from merchant_fssubaccount_mappping where memberid=? and fssubaccountid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberid);
            ps.setString(2,fssubaccountid);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Merchant Sub-account Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantFraudAccountUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Merchant Sub-account Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantFraudAccountUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean isMerchantFraudAccountAvailable(String memberId, String subaccountname) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT merchantfraudserviceid,mfm.fssubaccountid FROM merchant_fssubaccount_mappping AS mfm,\n" +
                    "fsaccount_subaccount_mapping AS fsm, fraudsystem_account_mapping AS fma,fraudsystem_master AS fm\n" +
                    "WHERE memberid=? AND fsm.subaccountname=? AND mfm.`fssubaccountid`=fsm.`fssubaccountid` AND fsm.`fsaccountid`=fma.`fsaccountid` AND fma.`fsid`= fm.`fsid` AND fm.`fsname`!='pz'\n");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,subaccountname);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while Checking Merchant Fraud Account Availability",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantFraudAccountAvailable()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  Checking Merchant Fraud Account Availability",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantFraudAccountAvailable()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public boolean isMerchantInternalFraudAccountAvailable(String memberId,String subaccountname) throws PZDBViolationException
    {
        Connection con = null;
        boolean status = true;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT merchantfraudserviceid,mfm.fssubaccountid FROM merchant_fssubaccount_mappping AS mfm,\n" +
                    "fsaccount_subaccount_mapping AS fsm, fraudsystem_account_mapping AS fma,fraudsystem_master AS fm\n" +
                    "WHERE memberid=? AND fsm.subaccountname=? AND mfm.`fssubaccountid`=fsm.`fssubaccountid` AND fsm.`fsaccountid`=fma.`fsaccountid` \n" +
                    "AND fma.`fsid`= fm.`fsid` AND fm.`fsname`='pz'");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,subaccountname);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status = false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while Checking Merchant Internal Fraud Account Availability",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantInternalFraudAccountAvailable()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  Checking Merchant Internal Fraud Account Availability",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isMerchantInternalFraudAccountAvailable()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }


    public FraudSystemAccountRuleVO getAccountLevelFraudRuleDetails(String fsAccountid,String ruleId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        FraudSystemAccountRuleVO fraudSystemAccountRuleVO=null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select arm.ruleid,arm.fsaccountid,arm.score,arm.status,arm.value,rm.rulename,fsam.accountname from account_rule_mapping as arm join rule_master as rm on arm.ruleid=rm.ruleid join fraudsystem_account_mapping as fsam on arm.fsaccountid=fsam.fsaccountid where arm.fsaccountid=? and arm.ruleid=? ");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,fsAccountid);
            pstmt.setString(2,ruleId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                fraudSystemAccountRuleVO=new FraudSystemAccountRuleVO();
                fraudSystemAccountRuleVO.setRuleId(rs.getString("ruleid"));
                fraudSystemAccountRuleVO.setFsAccountId(rs.getString("fsaccountid"));
                fraudSystemAccountRuleVO.setRuleName(rs.getString("rulename"));
                fraudSystemAccountRuleVO.setAccountName(rs.getString("accountname"));
                fraudSystemAccountRuleVO.setScore(rs.getString("score"));
                fraudSystemAccountRuleVO.setStatus(rs.getString("status"));
                fraudSystemAccountRuleVO.setValue(rs.getString("value"));
            }
        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while adding new Fraud Merchant Sub-account Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getAccountLevelFraudRuleDetails()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while  adding new Fraud Merchant Sub-account Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getAccountLevelFraudRuleDetails()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return fraudSystemAccountRuleVO;
    }

    public FraudSystemSubAccountRuleVO getSubAccountLevelFraudRuleDetails(String fsSubAccountid,String ruleId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO=null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fssam.subaccountname,sarm.fssubaccountid,sarm.ruleid,rm.rulename, fsam.accountname,sarm.score,sarm.status,sarm.value from subaccount_rule_mapping as sarm inner join fsaccount_subaccount_mapping as fssam on \n" +
                    "                    sarm.fssubaccountid=fssam.fssubaccountid  inner join  fraudsystem_account_mapping as fsam on fssam.fsaccountid=fsam.fsaccountid\n" +
                    "    inner join rule_master as rm on sarm.ruleid=rm.ruleid where sarm.fssubaccountid=? and sarm.ruleid=? ");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,fsSubAccountid);
            pstmt.setString(2,ruleId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                fraudSystemSubAccountRuleVO=new FraudSystemSubAccountRuleVO();
                fraudSystemSubAccountRuleVO.setRuleId(rs.getString("ruleid"));
                fraudSystemSubAccountRuleVO.setFsSubAccountId(rs.getString("fsSubAccountid"));
                fraudSystemSubAccountRuleVO.setRuleName(rs.getString("rulename"));
                fraudSystemSubAccountRuleVO.setSubAccountName(rs.getString("subaccountname"));
                fraudSystemSubAccountRuleVO.setScore(rs.getString("score"));
                fraudSystemSubAccountRuleVO.setStatus(rs.getString("status"));
                fraudSystemSubAccountRuleVO.setValue(rs.getString("value"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting fraud rule configuration details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSubAccountLevelFraudRuleDetails()", null, "Common", "Sql exception while connecting to subaccount_rule_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while getting fraud rule configuration details::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getSubAccountLevelFraudRuleDetails()", null, "Common", "Sql exception while connecting to subaccount_rule_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return fraudSystemSubAccountRuleVO;
    }


    public String updateAccountLevelFraudRule(FraudSystemAccountRuleVO fraudSystemAccountRuleVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update account_rule_mapping set score=?,status=?,value=? where fsaccountid=? and ruleid=? ");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,fraudSystemAccountRuleVO.getScore());
            pstmt.setString(2,fraudSystemAccountRuleVO.getStatus());
            if(functions.isValueNull(fraudSystemAccountRuleVO.getValue()))
            {
                pstmt.setString(3,fraudSystemAccountRuleVO.getValue());
            }
            else{
                pstmt.setNull(3,Types.INTEGER);
            }
            pstmt.setString(4,fraudSystemAccountRuleVO.getFsAccountId());
            pstmt.setString(5,fraudSystemAccountRuleVO.getRuleId());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }

        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while updating new Fraud Merchant account_rule_mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateAccountLevelFraudRule()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while  updating new Fraud Merchant account_rule_mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateAccountLevelFraudRule()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public FraudSystemAccountVO getFraudAccountDetails(String fsAccountId) throws PZDBViolationException
    {
        FraudSystemAccountVO accountVO = new FraudSystemAccountVO();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try
        {
            //con = Database.getConnection();
            con=Database.getRDBConnection();
            StringBuilder qry = new StringBuilder("select fsaccountid,accountname,username,password,fsid,isTest,contact_name,contact_email from fraudsystem_account_mapping where fsaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,fsAccountId);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                accountVO.setFraudSystemAccountId(rs.getString("fsaccountid"));
                accountVO.setAccountName(rs.getString("accountname"));
                accountVO.setUserName(rs.getString("username"));
                accountVO.setPassword(rs.getString("password"));
                accountVO.setFraudSystemId(rs.getString("fsid"));
                accountVO.setIsTest(rs.getString("isTest"));
                accountVO.setContactName(rs.getString("contact_name"));
                accountVO.setContactEmail(rs.getString("contact_email"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Fraud Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudAccountDetails()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting Fraud Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudAccountDetails()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return accountVO;
    }

    public String updateFraudAccount (FraudSystemAccountVO fraudSystemAccountVO) throws PZDBViolationException
    {
        String status = "fail";
        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update fraudsystem_account_mapping set username=?, password=?, isTest=?, contact_name=?, contact_email=? where fsaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,fraudSystemAccountVO.getUserName());
            pstmt.setString(2,fraudSystemAccountVO.getPassword());
            pstmt.setString(3,fraudSystemAccountVO.getIsTest());
            pstmt.setString(4,fraudSystemAccountVO.getContactName());
            pstmt.setString(5,fraudSystemAccountVO.getContactEmail());
            pstmt.setString(6,fraudSystemAccountVO.getFraudSystemAccountId());
            int k = pstmt.executeUpdate();
            if(k>0)
            {
                status = "success";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while updating Fraud Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updating Fraud Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String updateSubAccountLevelFraudRule(FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update subaccount_rule_mapping set score=?,status=?, value=? where fssubaccountid=? and ruleid=? ");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,fraudSystemSubAccountRuleVO.getScore());
            pstmt.setString(2,fraudSystemSubAccountRuleVO.getStatus());
            if(functions.isValueNull(fraudSystemSubAccountRuleVO.getValue()))
            {
                pstmt.setString(3, fraudSystemSubAccountRuleVO.getValue());
            }
            else
            {
                pstmt.setNull(3,Types.VARCHAR);
            }
            pstmt.setString(4,fraudSystemSubAccountRuleVO.getFsSubAccountId());
            pstmt.setString(5,fraudSystemSubAccountRuleVO.getRuleId());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }

        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while updating new Fraud Merchant account_rule_mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateSubAccountLevelFraudRule()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while updating new Fraud Merchant account_rule_mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateSubAccountLevelFraudRule()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;

    }

    public FraudSystemSubAccountVO getFraudSubAccountDetails(String fsSubAccountId, String fsaccountid) throws PZDBViolationException
    {
        FraudSystemSubAccountVO subAccountVO = new FraudSystemSubAccountVO();
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fssubaccountid,fsaccountid,subaccountname,subusername,subpwd,isactive from fsaccount_subaccount_mapping where fssubaccountid=? and fsaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,fsSubAccountId);
            pstmt.setString(2,fsaccountid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                subAccountVO.setFraudSystemSubAccountId(rs.getString("fssubaccountid"));
                subAccountVO.setFraudSystemAccountId(rs.getString("fsaccountid"));
                subAccountVO.setSubAccountName(rs.getString("subaccountname"));
                subAccountVO.setUserName(rs.getString("subusername"));
                subAccountVO.setPassword(rs.getString("subpwd"));
                subAccountVO.setIsActive(rs.getString("isactive"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Fraud Sub-Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudSubAccountDetails()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting Fraud Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudSubAccountDetails()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return subAccountVO;
    }

    public String updateFraudSubAccount (FraudSystemSubAccountVO subAccountVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";

        try
        {
            con = Database.getConnection();

            StringBuffer qry = new StringBuffer("update fsaccount_subaccount_mapping set subusername=?, subpwd=? where fssubaccountid=? and fsaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,subAccountVO.getUserName());
            pstmt.setString(2,subAccountVO.getPassword());
            pstmt.setString(3,subAccountVO.getFraudSystemSubAccountId());
            pstmt.setString(4,subAccountVO.getFraudSystemAccountId());
            k=pstmt.executeUpdate();

            if(k>0)
            {
                status="success";
            }

        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public MerchantFraudAccountVO getMerchantFraudAccountDetails(String merchantfraudserviceid, String fssubaccountid) throws PZDBViolationException
    {
        MerchantFraudAccountVO subAccountVO = new MerchantFraudAccountVO();
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select merchantfraudserviceid,memberid,mm.fssubaccountid,mm.isactive,mm.isvisible,m.subaccountname,mm.submerchantUsername,mm.submerchantPassword,isonlinefraudcheck,isapiuser from merchant_fssubaccount_mappping as mm, fsaccount_subaccount_mapping as m where mm.fssubaccountid=m.fssubaccountid and merchantfraudserviceid=? and mm.fssubaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,merchantfraudserviceid);
            pstmt.setString(2,fssubaccountid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                subAccountVO.setMerchantFraudAccountId(rs.getString("merchantfraudserviceid"));
                subAccountVO.setMemberId(rs.getString("memberid"));
                subAccountVO.setFsSubAccountId(rs.getString("fssubaccountid"));
                subAccountVO.setIsActive(rs.getString("isactive"));
                subAccountVO.setIsVisible(rs.getString("isvisible"));
                subAccountVO.setSubAccountName(rs.getString("subaccountname"));
                subAccountVO.setSubmerchantUsername(rs.getString("submerchantUsername"));
                subAccountVO.setSubmerchantPassword(rs.getString("submerchantPassword"));
                subAccountVO.setIsOnlineFraudCheck(rs.getString("isonlinefraudcheck"));
                subAccountVO.setIsAPIUser(rs.getString("isapiuser"));
                logger.error("Query::" + pstmt);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting merchant fraud account details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getMerchantFraudAccountDetails()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("Exception while getting merchant fraud account details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getMerchantFraudAccountDetails()", null, "Common", "Sql exception while connecting to merchant_fssubaccount_mappping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return subAccountVO;
    }

    public String updateMerchantFraudAccount (MerchantFraudAccountVO fraudAccountVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";

        try
        {
            con = Database.getConnection();

            StringBuffer qry = new StringBuffer("update merchant_fssubaccount_mappping set submerchantUsername=?, submerchantPassword=?, isactive=?,isvisible=?,isonlinefraudcheck=?,isapiuser=? where merchantfraudserviceid=? and fssubaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,fraudAccountVO.getSubmerchantUsername());
            pstmt.setString(2,fraudAccountVO.getSubmerchantPassword());
            pstmt.setString(3,fraudAccountVO.getIsActive());
            pstmt.setString(4,fraudAccountVO.getIsVisible());
            pstmt.setString(5,fraudAccountVO.getIsOnlineFraudCheck());
            pstmt.setString(6,fraudAccountVO.getIsAPIUser());
            pstmt.setString(7,fraudAccountVO.getMerchantFraudAccountId());
            pstmt.setString(8,fraudAccountVO.getFsSubAccountId());
            k=pstmt.executeUpdate();
            logger.error("query:::" + pstmt);

            if(k>0)
            {
                String fssubaccountid=fraudAccountVO.getFsSubAccountId();

                StringBuffer query1=new StringBuffer("update fsaccount_subaccount_mapping set submerchantUsername=?, submerchantPassword=? where fssubaccountid=?");
                pstmt=con.prepareStatement(query1.toString());
                pstmt.setString(1,fraudAccountVO.getSubmerchantUsername());
                pstmt.setString(2,fraudAccountVO.getSubmerchantPassword());
                pstmt.setString(3,fssubaccountid);
                int l=pstmt.executeUpdate();
                logger.error("query1::" + pstmt);
                if(l>0)
                {
                    status = "success";
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public String updateMerchantFraudAccountFROMPSP(MerchantFraudAccountVO fraudAccountVO) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";

        try
        {
            con = Database.getConnection();

            StringBuffer qry = new StringBuffer("update merchant_fssubaccount_mappping set submerchantUsername=?, submerchantPassword=?, isactive=?,isvisible=? where merchantfraudserviceid=? and fssubaccountid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,fraudAccountVO.getSubmerchantUsername());
            pstmt.setString(2,fraudAccountVO.getSubmerchantPassword());
            pstmt.setString(3,fraudAccountVO.getIsActive());
            pstmt.setString(4,fraudAccountVO.getIsVisible());
            pstmt.setString(5,fraudAccountVO.getMerchantFraudAccountId());
            pstmt.setString(6,fraudAccountVO.getFsSubAccountId());
            k=pstmt.executeUpdate();
            logger.error("query:::" + pstmt);

            if(k>0)
            {
                String fssubaccountid = fraudAccountVO.getFsSubAccountId();

                StringBuffer query1 = new StringBuffer("update fsaccount_subaccount_mapping set submerchantUsername=?, submerchantPassword=? where fssubaccountid=?");
                pstmt = con.prepareStatement(query1.toString());
                pstmt.setString(1, fraudAccountVO.getSubmerchantUsername());
                pstmt.setString(2, fraudAccountVO.getSubmerchantPassword());
                pstmt.setString(3, fssubaccountid);
                int l = pstmt.executeUpdate();
                logger.error("query1::" + pstmt);
                if (k > 0)
                {
                    status = "success";
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while updating Fraud Sub-Account Mapping Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateFraudSUbAccount()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String updateAccountRuleMapping (String score, String status, String fsaccountid, String ruleid) throws PZDBViolationException
    {
        String statusMsg = "fail";
        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update account_rule_mapping set score=?,status=?,timestamp=now() where fsaccountid=? and ruleid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,score);
            pstmt.setString(2,status);
            pstmt.setString(3,fsaccountid);
            pstmt.setString(4, ruleid);
            int k = pstmt.executeUpdate();
            if(k>0)
            {
                statusMsg = "success";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while updating Account Rule Mapping",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateAccountRuleMapping()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while updating Fraud Account Mapping Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateAccountRuleMapping()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return statusMsg;
    }
    public int createFraudRuleChangeIntimation(FraudRuleChangeIntimationVO intimationVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        int intimationId=0;
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into fraudrule_change_intimation(changeintimationid,fsid,fsaccountid,fssubaccountid,partnerid,memberid,status,creationdate) values(null,?,?,?,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,intimationVO.getFraudSystemId());
            pstmt.setString(2, intimationVO.getFsAccountId());
            pstmt.setString(3,intimationVO.getFsSubAccountId());
            pstmt.setString(4,intimationVO.getPartnerId());
            pstmt.setString(5,intimationVO.getMemberId());
            pstmt.setString(6,intimationVO.getStatus());
            pstmt.executeUpdate();
            ResultSet rs=pstmt.getGeneratedKeys();
            if(rs.next())
            {
                intimationId=rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while creating fraud rule change intimation",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "createFraudRuleChangeIntimation()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while creating fraud rule change intimation",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "createFraudRuleChangeIntimation()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return intimationId;
    }
    public String executeFraudRuleChangeTracker(FraudRuleChangeTrackerVO changeTrackerVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        String status="failed";
        int k=0;
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into fraudrule_change_tracker(fraudruletrackerid,fsid,fsaccountid,fssubaccountid," +
                    "ruleid,previousscore,newscore,previousstatus,newstatus,intimationid,dtstamp)values(null,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,changeTrackerVO.getFraudSystemId());
            pstmt.setString(2, changeTrackerVO.getFsAccountId());
            pstmt.setString(3,changeTrackerVO.getFsSubAccountId());
            pstmt.setString(4,changeTrackerVO.getRuleId());
            pstmt.setString(5, changeTrackerVO.getPreviousScore());
            pstmt.setString(6,changeTrackerVO.getNewScore());
            pstmt.setString(7,changeTrackerVO.getPreviousStatus());
            pstmt.setString(8,changeTrackerVO.getNewStatus());
            pstmt.setString(9,changeTrackerVO.getIntimationId());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while creating fraud rule change tracker",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "executeFraudRuleChangeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while creating fraud rule change tracker",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "executeFraudRuleChangeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public String handleSubAccountFraudRuleChangeRequest(SubAccountRuleChangeRequestVO changeRequestVO)throws PZDBViolationException
    {
        FraudRuleChangeIntimationVO intimationVO=changeRequestVO.getIntimationVO();
        List<FraudSystemSubAccountRuleVO> subAccountRuleVOList=changeRequestVO.getSubAccountRuleVOList();

        Connection con = null;
        PreparedStatement pstmt= null;
        int intimationId=0;
        int k=0;
        StringBuilder sb=new StringBuilder();
        try
        {
            con= Database.getConnection();
            Database.setAutoCommit(con, false);

            StringBuilder query =new StringBuilder("insert into fraudrule_change_intimation(changeintimationid,fsid,fsaccountid,fssubaccountid,partnerid,memberid,status,creationdate) values(null,?,?,?,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,intimationVO.getFraudSystemId());
            pstmt.setString(2, intimationVO.getFsAccountId());
            pstmt.setString(3,intimationVO.getFsSubAccountId());
            pstmt.setString(4,intimationVO.getPartnerId());
            pstmt.setString(5,intimationVO.getMemberId());
            pstmt.setString(6,intimationVO.getStatus());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                ResultSet rs=pstmt.getGeneratedKeys();
                rs.next();
                intimationId=rs.getInt(1);
                for(FraudSystemSubAccountRuleVO subAccountRuleVO:subAccountRuleVOList)
                {
                    FraudSystemSubAccountRuleVO previousSubAccountRuleVO=getSubAccountLevelFraudRuleDetails(changeRequestVO.getFraudSystemSubAccountId(), subAccountRuleVO.getRuleId());
                    query =new StringBuilder("update subaccount_rule_mapping set score=?,status=? where fssubaccountid=? and ruleid=?");
                    pstmt=con.prepareStatement(query.toString());
                    pstmt.setString(1,subAccountRuleVO.getScore());
                    pstmt.setString(2,subAccountRuleVO.getStatus());
                    pstmt.setString(3,changeRequestVO.getFraudSystemSubAccountId());
                    pstmt.setString(4,subAccountRuleVO.getRuleId());
                    k=pstmt.executeUpdate();
                    if(k>0)
                    {
                        query =new StringBuilder("insert into fraudrule_change_tracker(fraudruletrackerid,fsid,fsaccountid,fssubaccountid,").append("ruleid,previousscore,newscore,previousstatus,newstatus,intimationid,dtstamp)values(null,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))");
                        pstmt=con.prepareStatement(query.toString());
                        pstmt.setString(1, changeRequestVO.getFraudSystemId());
                        pstmt.setString(2,changeRequestVO.getFraudSystemAccountId());
                        pstmt.setString(3,changeRequestVO.getFraudSystemSubAccountId());
                        pstmt.setString(4,subAccountRuleVO.getRuleId());
                        pstmt.setString(5,previousSubAccountRuleVO.getScore());
                        pstmt.setString(6,subAccountRuleVO.getScore());
                        pstmt.setString(7,previousSubAccountRuleVO.getStatus());
                        pstmt.setString(8,subAccountRuleVO.getStatus());
                        pstmt.setInt(9,intimationId);
                        pstmt.executeUpdate();
                    }
                }
                //if everything is fine call commit method.
                sb.append("success");
                Database.commit(con);

            }
        }
        catch (SQLException e)
        {
            sb.append("fail");
            Database.rollback(con);
            logger.error("SQL Exception while handling subaccount fraud rule change request",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "handleSubAccountFraudRuleChangeRequest()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            sb.append("fail");
            Database.rollback(con);
            logger.error("System Error while handling subaccount fraud rule change request",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "handleSubAccountFraudRuleChangeRequest()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return sb.toString();
    }
    public String handleAccountFraudRuleChangeRequest(AccountRuleChangeRequestVO changeRequestVO)throws PZDBViolationException
    {
        FraudRuleChangeIntimationVO intimationVO=changeRequestVO.getIntimationVO();
        List<FraudSystemAccountRuleVO> accountRuleVOList=changeRequestVO.getSystemAccountRuleVOList();

        Connection con = null;
        PreparedStatement pstmt= null;
        int intimationId=0;
        int k=0;
        StringBuffer sb=new StringBuffer();
        try
        {
            con= Database.getConnection();
            Database.setAutoCommit(con, false);

            StringBuffer query =new StringBuffer("insert into fraudrule_change_intimation(changeintimationid,fsid,fsaccountid,status,creationdate) values(null,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,intimationVO.getFraudSystemId());
            pstmt.setString(2,intimationVO.getFsAccountId());
            pstmt.setString(3, intimationVO.getStatus());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                ResultSet rs=pstmt.getGeneratedKeys();
                rs.next();
                intimationId=rs.getInt(1);
                for(FraudSystemAccountRuleVO accountRuleVO:accountRuleVOList)
                {
                    FraudSystemAccountRuleVO previousAccountRuleVO = getAccountLevelFraudRuleDetails(changeRequestVO.getFraudSystemAccountId(), accountRuleVO.getRuleId());
                    query =new StringBuffer("update account_rule_mapping set score=?,status=? where fsaccountid=? and ruleid=?");
                    pstmt=con.prepareStatement(query.toString());
                    pstmt.setString(1,accountRuleVO.getScore());
                    pstmt.setString(2,accountRuleVO.getStatus());
                    pstmt.setString(3, changeRequestVO.getFraudSystemAccountId());
                    pstmt.setString(4, accountRuleVO.getRuleId());
                    k=pstmt.executeUpdate();
                    if(k>0)
                    {
                        query =new StringBuffer("insert into fraudrule_change_tracker(fraudruletrackerid,fsid,fsaccountid," +
                                "ruleid,previousscore,newscore,previousstatus,newstatus,intimationid,dtstamp)values(null,?,?,?,?,?,?,?,?,unix_timestamp(now()))");
                        pstmt=con.prepareStatement(query.toString());
                        pstmt.setString(1,changeRequestVO.getFraudSystemId());
                        pstmt.setString(2, changeRequestVO.getFraudSystemAccountId());
                        pstmt.setString(3,accountRuleVO.getRuleId());
                        pstmt.setString(4,previousAccountRuleVO.getScore());
                        pstmt.setString(5, accountRuleVO.getScore());
                        pstmt.setString(6, previousAccountRuleVO.getStatus());
                        pstmt.setString(7, accountRuleVO.getStatus());
                        pstmt.setInt(8, intimationId);
                        pstmt.executeUpdate();
                    }
                }
                //if everything is fine call commit method.
                sb.append("success");
                Database.commit(con);

            }
        }
        catch (SQLException e)
        {
            sb.append("fail");
            Database.rollback(con);
            logger.error("SQL Exception while handling account fraud rule change request",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "handleAccountFraudRuleChangeRequest()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            sb.append("fail");
            Database.rollback(con);
            logger.error("System Error while handling account fraud rule change request",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "handleAccountFraudRuleChangeRequest()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return sb.toString();
    }

    public List<FraudRuleChangeIntimationVO> getFraudRuleChargeIntimation(String fsaccountid,String status, PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        ResultSet rs=null;
        List<FraudRuleChangeIntimationVO> intimationVOsList = new ArrayList<FraudRuleChangeIntimationVO>();
        FraudRuleChangeIntimationVO intimationVO = null;
        Functions functions=new Functions();
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select *,from_unixtime(creationdate) as creationon from fraudrule_change_intimation where fsaccountid=?");
            StringBuffer countQry = new StringBuffer("select count(changeintimationid) from fraudrule_change_intimation where fsaccountid=?");
            if(functions.isValueNull(status))
            {
                qry.append(" and status= '" +ESAPI.encoder().encodeForSQL(me,status) + "'");
                countQry.append(" and status= '" +ESAPI.encoder().encodeForSQL(me,status) + "'");
            }
            qry.append(" order by changeintimationid desc limit " + paginationVO.getStart() + "," + paginationVO.getEnd());
            countQry.append(" order by changeintimationid desc");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, fsaccountid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                intimationVO = new FraudRuleChangeIntimationVO();
                intimationVO.setChangeIntimationId(rs.getString("changeintimationid"));
                intimationVO.setFraudSystemId(rs.getString("fsid"));
                intimationVO.setFsAccountId(rs.getString("fsaccountid"));
                intimationVO.setFsSubAccountId(rs.getString("fssubaccountid") != null ? rs.getString("fssubaccountid") : "");
                intimationVO.setPartnerId(rs.getString("partnerid") != null ? rs.getString("partnerid") : "");
                intimationVO.setMemberId(rs.getString("memberid") != null ? rs.getString("memberid") : "");
                intimationVO.setStatus(rs.getString("status"));
                intimationVO.setCreationDate(rs.getString("creationon"));
                intimationVOsList.add(intimationVO);
            }
            pstmt = con.prepareStatement(countQry.toString());
            pstmt.setString(1, fsaccountid);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Fraud Rule Change Intimation Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeIntimation()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting Fraud Rule Change Intimation Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeIntimation()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return intimationVOsList;
    }

    /*public List<FraudRuleChangeTrackerVO> getFraudRuleChangeTracker(String[] intimationids)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement subpstmt = null;
        ResultSet subrs = null;
        String allIntimationId = null;
        StringBuffer sb = new StringBuffer();
        List<FraudRuleChangeTrackerVO> changeTrackerVOsList = new ArrayList<FraudRuleChangeTrackerVO>();
        FraudRuleChangeTrackerVO changeTrackerVO = null;
        try
        {
            con = Database.getConnection();
            for(int id=0; id<intimationids.length; id++)
            {
                sb.append("'"+intimationids[id]+ "',");
            }
            allIntimationId = sb.toString();
            allIntimationId = allIntimationId.substring(0, allIntimationId.length() - 1);

            StringBuffer accountQry = new StringBuffer("select fct.ruleid,fct.previousscore,fct.newscore,fct.previousstatus,fct.newstatus,rm.rulename,fct.fssubaccountid,fct.dtstamp \n" +
                    "from fraudrule_change_tracker as fct,rule_master as rm\n" +
                    "where intimationid in ("+allIntimationId+") and fct.ruleid=rm.ruleid order by  fssubaccountid");
            pstmt = con.prepareStatement(accountQry.toString());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                changeTrackerVO = new FraudRuleChangeTrackerVO();
                changeTrackerVO.setRuleId(rs.getString("ruleid"));
                changeTrackerVO.setPreviousScore(rs.getString("previousscore"));
                changeTrackerVO.setNewScore(rs.getString("newscore"));
                changeTrackerVO.setPreviousStatus(rs.getString("previousstatus"));
                changeTrackerVO.setNewStatus(rs.getString("newstatus"));
                changeTrackerVO.setRuleName(rs.getString("rulename"));
                if(rs.getString("fssubaccountid") != null)
                {
                    StringBuffer subaccountQry = new StringBuffer("select * from fsaccount_subaccount_mapping where fssubaccountid=?");
                    subpstmt = con.prepareStatement(subaccountQry.toString());
                    subpstmt.setString(1, rs.getString("fssubaccountid"));
                    subrs = subpstmt.executeQuery();
                    if(subrs.next())
                    {
                        changeTrackerVO.setFsSubAccountName(subrs.getString("subaccountname"));
                    }
                }
                changeTrackerVOsList.add(changeTrackerVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting Fraud Rule Charge Tracker Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Fraud Rule Charge Tracker Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return changeTrackerVOsList;
    }*/

    public List<FraudRuleChangeTrackerVO> getFraudRuleChangeTracker(String[] intimationids)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String allIntimationId = null;
        StringBuffer sb = new StringBuffer();
        List<FraudRuleChangeTrackerVO> changeTrackerVOsList = new ArrayList<FraudRuleChangeTrackerVO>();
        FraudRuleChangeTrackerVO changeTrackerVO = null;
        try
        {
            con = Database.getConnection();
            int i=0;
            for(int id=0; id<intimationids.length; id++)
            {
                if(i!=0)
                {
                    sb.append(",");
                }
                sb.append("'"+intimationids[id]+ "'");
                i++;
            }
            allIntimationId = sb.toString();

            //account level rules - checking for duplication and take the latest record
            StringBuffer accRulesDuplicateQry = new StringBuffer("SELECT ruleid, COUNT(*) FROM fraudrule_change_tracker where fssubaccountid is null and intimationid in("+allIntimationId+") GROUP BY ruleid ");
            PreparedStatement accRulesDuplicatePs = con.prepareStatement(accRulesDuplicateQry.toString());
            ResultSet accRulesDuplicateRset = accRulesDuplicatePs.executeQuery();
            while (accRulesDuplicateRset.next())
            {
                //changeTrackerVO = getAccoutRuleChangeTracker(accRulesDuplicateRset.getString("ruleid"));
                StringBuffer qry = new StringBuffer("select fct.ruleid,fct.previousscore,fct.newscore,fct.previousstatus,fct.newstatus,rm.rulename,fct.fssubaccountid,fct.dtstamp\n" +
                        "from fraudrule_change_tracker as fct,rule_master as rm where fct.ruleid=? and fct.ruleid=rm.ruleid ORDER BY fct.dtstamp DESC LIMIT 1");
                pstmt = con.prepareStatement(qry.toString());
                pstmt.setString(1,accRulesDuplicateRset.getString("ruleid"));
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    changeTrackerVO = new FraudRuleChangeTrackerVO();
                    changeTrackerVO.setRuleId(rs.getString("ruleid"));
                    changeTrackerVO.setPreviousScore(rs.getString("previousscore"));
                    changeTrackerVO.setNewScore(rs.getString("newscore"));
                    changeTrackerVO.setPreviousStatus(rs.getString("previousstatus"));
                    changeTrackerVO.setNewStatus(rs.getString("newstatus"));
                    changeTrackerVO.setRuleName(rs.getString("rulename"));
                }
                changeTrackerVOsList.add(changeTrackerVO);
            }

            //subaccount level rules
            StringBuffer subAccRulesDuplicateQry = new StringBuffer("SELECT ruleid,fssubaccountid, COUNT(*) FROM fraudrule_change_tracker where fssubaccountid is not null and intimationid in ("+allIntimationId+") GROUP BY ruleid, fssubaccountid order by fssubaccountid");
            PreparedStatement subAccRulesDuplicatePs = con.prepareStatement(subAccRulesDuplicateQry.toString());
            ResultSet subAccRulesDuplicateRs = subAccRulesDuplicatePs.executeQuery();
            while (subAccRulesDuplicateRs.next())
            {
                //changeTrackerVO = getSubAccoutRuleChangeTracker(subAccRulesDuplicateRs.getString("ruleid"),subAccRulesDuplicateRs.getString("fssubaccountid"));
                StringBuffer qry = new StringBuffer("select fct.ruleid,fct.previousscore,fct.newscore,fct.previousstatus,fct.newstatus,rm.rulename,fct.fssubaccountid,fct.dtstamp\n" +
                        "from fraudrule_change_tracker as fct,rule_master as rm where fct.ruleid=? and fssubaccountid=? and fct.ruleid=rm.ruleid ORDER BY fct.dtstamp DESC LIMIT 1");
                pstmt = con.prepareStatement(qry.toString());
                pstmt.setString(1,subAccRulesDuplicateRs.getString("ruleid"));
                pstmt.setString(2,subAccRulesDuplicateRs.getString("fssubaccountid"));
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    changeTrackerVO = new FraudRuleChangeTrackerVO();
                    changeTrackerVO.setRuleId(rs.getString("ruleid"));
                    changeTrackerVO.setPreviousScore(rs.getString("previousscore"));
                    changeTrackerVO.setNewScore(rs.getString("newscore"));
                    changeTrackerVO.setPreviousStatus(rs.getString("previousstatus"));
                    changeTrackerVO.setNewStatus(rs.getString("newstatus"));
                    changeTrackerVO.setRuleName(rs.getString("rulename"));
                    StringBuffer subAccNameQry = new StringBuffer("select * from fsaccount_subaccount_mapping where fssubaccountid=?");
                    PreparedStatement subAccNamePs = con.prepareStatement(subAccNameQry.toString());
                    subAccNamePs.setString(1, rs.getString("fssubaccountid"));
                    ResultSet subAccNameRs = subAccNamePs.executeQuery();
                    if(subAccNameRs.next())
                    {
                        changeTrackerVO.setFsSubAccountName(subAccNameRs.getString("subaccountname"));
                    }
                }
                changeTrackerVOsList.add(changeTrackerVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting Fraud Rule Charge Tracker Details",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Fraud Rule Charge Tracker Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudRuleChargeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return changeTrackerVOsList;
    }

    public FraudRuleChangeTrackerVO getSubAccoutRuleChangeTracker(String ruleid, String fssubaccountid) throws PZDBViolationException
    {
        FraudRuleChangeTrackerVO changeTrackerVO = new FraudRuleChangeTrackerVO();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fct.ruleid,fct.previousscore,fct.newscore,fct.previousstatus,fct.newstatus,rm.rulename,fct.fssubaccountid,fct.dtstamp\n" +
                    "from fraudrule_change_tracker as fct,rule_master as rm where fct.ruleid=? and fssubaccountid=? and fct.ruleid=rm.ruleid ORDER BY max(fct.dtstamp)");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,ruleid);
            pstmt.setString(2,fssubaccountid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                changeTrackerVO = new FraudRuleChangeTrackerVO();
                changeTrackerVO.setRuleId(rs.getString("ruleid"));
                changeTrackerVO.setPreviousScore(rs.getString("previousscore"));
                changeTrackerVO.setNewScore(rs.getString("newscore"));
                changeTrackerVO.setPreviousStatus(rs.getString("previousstatus"));
                changeTrackerVO.setNewStatus(rs.getString("newstatus"));
                changeTrackerVO.setRuleName(rs.getString("rulename"));
                StringBuffer subAccNameQry = new StringBuffer("select * from fsaccount_subaccount_mapping where fssubaccountid=?");
                PreparedStatement subAccNamePs = con.prepareStatement(subAccNameQry.toString());
                subAccNamePs.setString(1, rs.getString("fssubaccountid"));
                ResultSet subAccNameRs = subAccNamePs.executeQuery();
                if(subAccNameRs.next())
                {
                    changeTrackerVO.setFsSubAccountName(subAccNameRs.getString("subaccountname"));
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError :" , systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException :" , e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return changeTrackerVO;
    }

    public FraudRuleChangeTrackerVO getAccoutRuleChangeTracker(String ruleid) throws PZDBViolationException
    {
        FraudRuleChangeTrackerVO changeTrackerVO = new FraudRuleChangeTrackerVO();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con =Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select fct.ruleid,fct.previousscore,fct.newscore,fct.previousstatus,fct.newstatus,rm.rulename,fct.fssubaccountid,fct.dtstamp\n" +
                    "from fraudrule_change_tracker as fct,rule_master as rm where fct.ruleid=? and fct.ruleid=rm.ruleid ORDER BY max(fct.dtstamp)");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,ruleid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                changeTrackerVO = new FraudRuleChangeTrackerVO();
                changeTrackerVO.setRuleId(rs.getString("ruleid"));
                changeTrackerVO.setPreviousScore(rs.getString("previousscore"));
                changeTrackerVO.setNewScore(rs.getString("newscore"));
                changeTrackerVO.setPreviousStatus(rs.getString("previousstatus"));
                changeTrackerVO.setNewStatus(rs.getString("newstatus"));
                changeTrackerVO.setRuleName(rs.getString("rulename"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting Account level Fraud Rule Charge Tracker Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getAccoutRuleChangeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError e)
        {
            logger.error("SQL Exception while getting Account level Fraud Rule Charge Tracker Details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getAccoutRuleChangeTracker()", null, "Common", "Sql exception while connecting to fraudrule_change_tracker", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return changeTrackerVO;
    }

    public String updateRuleIntimationStatus (String intimationid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        String status = "Intimated";
        String statusMsg = "false";
        int k = 0;
        try
        {
            con = Database.getConnection();
            StringBuffer qry = new StringBuffer("update fraudrule_change_intimation set status=? where changeintimationid=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,status);
            pstmt.setString(2,intimationid);
            k = pstmt.executeUpdate();
            if(k > 0)
            {
                statusMsg = "true";
            }
        }
        catch (SystemError systemError)
        {
            statusMsg = "false";
            logger.error("SQL Exception while updating Fraud Rule Charge Intimation Status",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateRuleIntimationStatus()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            statusMsg = "false";
            logger.error("SQL Exception while updating Fraud Rule Charge Intimation Status",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "updateRuleIntimationStatus()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return statusMsg;
    }

    public String isStatusIntimated ( String[] intimationids) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = "Intimated";
        String statusMsg = "false";
        StringBuffer sb = new StringBuffer();
        String allIntimationId = null;

        try
        {
            con = Database.getRDBConnection();
            for(int id=0; id<intimationids.length; id++)
            {
                sb.append("'"+intimationids[id]+ "',");
            }
            allIntimationId = sb.toString();
            allIntimationId = allIntimationId.substring(0, allIntimationId.length() - 1);

            /*for(String intimationid : intimationids)
            {*/
            StringBuffer qry = new StringBuffer("select status from fraudrule_change_intimation where status=? and changeintimationid in (" +allIntimationId+")");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                statusMsg = "true";
                return statusMsg;
            }
            //}
        }
        catch (SystemError systemError)
        {
            statusMsg = "false";
            logger.error("SQL Exception while getting Fraud Rule Charge Intimation Status",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isStatusIntimated()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            statusMsg = "false";
            logger.error("SQL Exception while getting Fraud Rule Charge Intimation Status",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isStatusIntimated()", null, "Common", "Sql exception while connecting to fraudrule_change_intimation", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return statusMsg;
    }
    public boolean isMerchantUnique(String mid)throws  PZDBViolationException
    {
        Connection con = null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select memberid from merchant_fssubaccount_mappping where memberid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,mid);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public boolean isFraudAccountUnique(String fssubaccountid)throws  PZDBViolationException
    {
        Connection con = null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select memberid from merchant_fssubaccount_mappping where fssubaccountid=?");
            PreparedStatement ps = con.prepareStatement(qry.toString());
            ps.setString(1,fssubaccountid);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        catch (SQLException e)
        {
            status=false;
            logger.error("SQL Exception while adding new Fraud Sub-account rule Mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            status=false;
            logger.error("System Error while  adding new Fraud Sub-account rule Mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "isFraudSubAccountRuleMapUnique()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public String addNewFraudSystemSubAccountFROMPSP(FraudSystemSubAccountVO  subAccountVO)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs = null;
        int k=0;
        String queryStatus="fail";
        try
        {
            con= Database.getConnection();
            StringBuffer query =new StringBuffer("insert into fsaccount_subaccount_mapping(fssubaccountid,fsaccountid,subaccountname,submerchantUsername,submerchantPassword,isactive,dtstamp)" +
                    "values(null,?,?,?,?,?,unix_timestamp(now()))");
            pstmt=con.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, Integer.parseInt(subAccountVO.getFraudSystemAccountId()));
            pstmt.setString(2,subAccountVO.getSubAccountName());
            pstmt.setString(3,subAccountVO.getSubmerchantUsername());
            pstmt.setString(4, subAccountVO.getSubmerchantPassword());
            pstmt.setString(5,subAccountVO.getIsActive());
            k=pstmt.executeUpdate();
            logger.error("Query:::"+pstmt);
            if(k>0)
            {
                rs=pstmt.getGeneratedKeys();
                rs.next();
                int fssubaccountid=rs.getInt(1);

                String query1 = "insert into merchant_fssubaccount_mappping(merchantfraudserviceid,memberid,fssubaccountid,submerchantUsername,submerchantPassword,isactive,dtstamp,isvisible,isonlinefraudcheck,isapiuser)VALUES (null,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
                pstmt = con.prepareStatement(query1);
                pstmt.setString(1, subAccountVO.getMerchantFraudAccountVO().getMemberId());
                pstmt.setInt(2, fssubaccountid);
                pstmt.setString(3,subAccountVO.getSubmerchantUsername());
                pstmt.setString(4, subAccountVO.getSubmerchantPassword());
                pstmt.setString(5,subAccountVO.getMerchantFraudAccountVO().getIsActive());
                pstmt.setString(6,subAccountVO.getMerchantFraudAccountVO().getIsVisible());
                pstmt.setString(7,subAccountVO.getMerchantFraudAccountVO().getIsOnlineFraudCheck());
                pstmt.setString(8,subAccountVO.getMerchantFraudAccountVO().getIsAPIUser());
                int l=pstmt.executeUpdate();


                if (l > 0)
                {
                    queryStatus = "success";

                    StringBuffer query2 = new StringBuffer("SELECT fm.fsname FROM `fraudsystem_master` AS fm , `fraudsystem_account_mapping` AS fam , `fsaccount_subaccount_mapping` AS fsm WHERE fsm.fssubaccountid=? AND fsm.fsaccountid = fam.fsaccountid AND fam.fsid = fm.fsid;");
                    pstmt = con.prepareStatement(query2.toString());
                    pstmt.setInt(1, fssubaccountid);
                    ResultSet rs1 = pstmt.executeQuery();
                    if (rs1.next() && rs1.getString(1).equalsIgnoreCase("pz"))
                    {
                        StringBuffer query3 = new StringBuffer("INSERT INTO subaccount_rule_mapping (fssubaccountid,ruleid,score,`status`)\n" +
                                "  SELECT  fsaccount_subaccount_mapping.`fssubaccountid` AS fssubaccountid, rule_master.`ruleid` AS ruleid,`rule_master`.`score` AS score, `rule_master`.`status` AS `status` \n" +
                                "  FROM rule_master, fsaccount_subaccount_mapping WHERE fsaccount_subaccount_mapping.`fssubaccountid`=? AND rule_master.`rulegroup`='Internal'");
                        pstmt = con.prepareStatement(query3.toString());
                        pstmt.setInt(1, fssubaccountid);
                        int m = pstmt.executeUpdate();
                        if (m > 0)
                        {
                            queryStatus = "success";
                        }
                    }
                    else
                    {
                        if (!functions.isValueNull(subAccountVO.getSubmerchantUsername()) && !functions.isValueNull(subAccountVO.getSubmerchantPassword()))
                        {
                            StringBuffer query4 = new StringBuffer("INSERT INTO subaccount_rule_mapping (fssubaccountid,ruleid,score,`status`)\n" +
                                    "  SELECT  fsaccount_subaccount_mapping.`fssubaccountid` AS fssubaccountid, rule_master.`ruleid` AS ruleid,`rule_master`.`score` AS score, `rule_master`.`status` AS `status` \n" +
                                    "  FROM rule_master, fsaccount_subaccount_mapping WHERE fsaccount_subaccount_mapping.`fssubaccountid`=? AND rule_master.`rulegroup`='Dynamic'");
                            pstmt = con.prepareStatement(query4.toString());
                            pstmt.setInt(1, fssubaccountid);
                            int m = pstmt.executeUpdate();
                            if (m > 0)
                            {
                                queryStatus = "success";
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            queryStatus="fail";
            logger.error("SQL Exception while creating merchant fraud account",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemSubAccountFROMPSP()", null, "Common", "Sql exception while connecting to fsaccount table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            queryStatus="fail";
            logger.error("System Error while creating creating merchant fraud account ",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "addNewFraudSystemSubAccountFROMPSP()", null, "Common", "Sql exception while connecting to fsaccount's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return queryStatus;
    }

    public List<RuleMasterVO> getInternalSubLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            //StringBuilder query =new StringBuilder("SELECT DISTINCT sarm.ruleid,rm.rulename,rm.ruledescription ,sarm.score,sarm.value,sarm.status , mf.`isactive` FROM `rule_master` AS rm JOIN subaccount_rule_mapping AS sarm ON rm.ruleid = sarm.ruleid JOIN `fsaccount_subaccount_mapping` AS fs ON fs.fssubaccountid = sarm.fssubaccountid JOIN `merchant_fssubaccount_mappping` AS mf ON mf.fssubaccountid = fs.fssubaccountid WHERE mf.memberid =? AND rm.rulegroup='Internal'");
            StringBuilder query =new StringBuilder("SELECT DISTINCT sarm.ruleid,rm.rulename,rm.ruledescription ,sarm.score,sarm.value,sarm.status , mf.`isactive` FROM `rule_master` AS rm JOIN subaccount_rule_mapping AS sarm ON rm.ruleid = sarm.ruleid JOIN `merchant_fssubaccount_mappping` AS mf ON mf.fssubaccountid = sarm.fssubaccountid WHERE mf.memberid =? AND rm.rulegroup='Internal';");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,merchantid);
            logger.debug("memberid pstmt::::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setRuleDescription(rs.getString("ruledescription"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setDefaultValue(rs.getString("value"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));
                //ruleMasterVO.setIsActive(rs.getString("isactive"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting internal level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }
    public List<RuleMasterVO> getInternalAccountLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT DISTINCT ar.ruleid,rm.rulename,rm.ruledescription ,ar.score,ar.value,ar.status,mem.`partnerId` FROM `rule_master` AS rm JOIN `account_rule_mapping` AS ar ON rm.ruleid = ar.ruleid JOIN `partner_fsaccounts_mapping` AS pfs ON pfs.fsaccountid = ar.fsaccountid JOIN members AS mem ON mem.`partnerId` = pfs.`partnerid` WHERE mem.`memberid`= ? AND rm.rulegroup='Internal'");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,merchantid);
            transactionLogger.error("getInternalAccountLevelRiskRuleList pstmt::::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setRuleDescription(rs.getString("ruledescription"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setDefaultValue(rs.getString("value"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));
                //ruleMasterVO.setIsActive(rs.getString("isactive"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting internal level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }

    public List<RuleMasterVO> getPartnerInternalLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
           // StringBuilder query =new StringBuilder("SELECT DISTINCT sarm.ruleid,rm.rulename,rm.ruledescription ,sarm.score,sarm.value,sarm.status , mf.`isactive` FROM `rule_master` AS rm JOIN `subaccount_rule_mapping` AS sarm ON rm.ruleid = sarm.ruleid JOIN `fsaccount_subaccount_mapping` AS fs ON fs.fssubaccountid = sarm.fssubaccountid JOIN `merchant_fssubaccount_mappping` AS mf ON mf.fssubaccountid = fs.fssubaccountid AND sarm.fssubaccountid=? WHERE rm.rulegroup='Internal'");
            StringBuilder query =new StringBuilder("SELECT DISTINCT sarm.ruleid,rm.rulename,rm.ruledescription ,sarm.score,sarm.value,sarm.status , mf.`isactive` FROM `rule_master` AS rm JOIN subaccount_rule_mapping AS sarm ON rm.ruleid = sarm.ruleid JOIN `merchant_fssubaccount_mappping` AS mf ON mf.fssubaccountid = sarm.fssubaccountid WHERE mf.memberid =? AND rm.rulegroup='Internal'");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,merchantid);
            transactionLogger.debug("merchantid pstmt::::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setRuleDescription(rs.getString("ruledescription"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setDefaultValue(rs.getString("value"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));
                ruleMasterVO.setIsActive(rs.getString("isactive"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting internal level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerInternalLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }

    public FraudSystemSubAccountVO getFraudServicePartnerAccountDetails(String fsaccountid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        FraudSystemSubAccountVO subAccountVO=null;
        try
        {
            //System.out.println("inside getFraudServicePartnerAccountDetails");
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT accountruleid,fsaccountid,ruleid, FROM_UNIXTIME(TIMESTAMP) FROM account_rule_mapping WHERE fsaccountid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,fsaccountid);
            logger.debug("rule pstmt:::::" + pstmt);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                subAccountVO=new FraudSystemSubAccountVO();
                subAccountVO.setAccountruleid(rs.getString("accountruleid"));
                subAccountVO.setFraudSystemAccountId(rs.getString("fsaccountid"));
                subAccountVO.setRuleid(rs.getString("ruleid"));
                //System.out.println("Query::" +pstmt);

            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting partner fraud account details",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudServicePartnerAccountDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting partner fraud account details::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getFraudServicePartnerAccountDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
           // System.out.println("outside getFraudServicePartnerAccountDetails");
        }
        return subAccountVO;
    }

    public List<RuleMasterVO> getPartnerInternalAccountLevelRiskRuleList(String partnerid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<RuleMasterVO>();
        RuleMasterVO ruleMasterVO=null;
        try
        {
            //con= Database.getConnection();
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT DISTINCT ar.ruleid,rm.rulename,rm.ruledescription ,ar.score,ar.value,ar.status,pfs.`partnerId` FROM `rule_master` AS rm JOIN `account_rule_mapping` AS ar ON rm.ruleid = ar.ruleid JOIN `partner_fsaccounts_mapping` AS pfs ON pfs.fsaccountid = ar.fsaccountid  WHERE pfs.`partnerId`= ? AND rm.rulegroup='Internal'");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,partnerid);
            logger.debug("partnerid pstmt::::::" + pstmt);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleId(rs.getString("ruleid"));
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setRuleDescription(rs.getString("ruledescription"));
                ruleMasterVO.setDefaultScore(rs.getString("score"));
                ruleMasterVO.setDefaultValue(rs.getString("value"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting internal level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getPartnerInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return ruleMasterVOList;
    }

    public String getUpdateInternalAccountLevelRiskRuleList(List<RuleMasterVO> list, String partnerid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";
        try
        {
            con = Database.getConnection();
            for (RuleMasterVO ruleMasterVO:list)
            {
                StringBuffer qry = new StringBuffer("UPDATE account_rule_mapping AS arm JOIN partner_fsaccounts_mapping AS pfs ON pfs.fsaccountid=arm.fsaccountid SET arm.score=?, arm.value=?, arm.status=? WHERE arm.ruleid=? AND pfs.partnerid=?");
                pstmt = con.prepareStatement(qry.toString());
                pstmt.setString(1, ruleMasterVO.getDefaultScore());
                if (functions.isValueNull(ruleMasterVO.getDefaultValue()))
                {
                    pstmt.setString(2, ruleMasterVO.getDefaultValue());
                }
                else
                {
                    pstmt.setNull(2, Types.INTEGER);
                }
                pstmt.setString(3, ruleMasterVO.getDefaultStatus());
                pstmt.setString(4, ruleMasterVO.getRuleId());
                pstmt.setString(5, partnerid);
                k = pstmt.executeUpdate();
                if (k > 0)
                {
                    status = "success";
                }
            }

        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while updating new Fraud Merchant account_rule_mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getUpdateInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while updating new Fraud Merchant account_rule_mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getUpdateInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String getUpdateInternalSubAccountLevelRiskRuleList(List<RuleMasterVO> list, String merchantid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        String status="fail";
        try
        {
            con = Database.getConnection();
            for (RuleMasterVO ruleMasterVO:list)
            {
                StringBuffer qry = new StringBuffer("UPDATE subaccount_rule_mapping AS sarm JOIN `merchant_fssubaccount_mappping` AS mfs ON mfs.fssubaccountid=sarm.fssubaccountid SET sarm.score=?, sarm.value=?, sarm.status=? WHERE sarm.ruleid=? AND mfs.memberid=? ");
                pstmt = con.prepareStatement(qry.toString());
                pstmt.setString(1, ruleMasterVO.getDefaultScore());
                if (functions.isValueNull(ruleMasterVO.getDefaultValue()))
                {
                    pstmt.setString(2, ruleMasterVO.getDefaultValue());
                }
                else
                {
                    pstmt.setNull(2, Types.INTEGER);
                }
                pstmt.setString(3, ruleMasterVO.getDefaultStatus());
                pstmt.setString(4, ruleMasterVO.getRuleId());
                pstmt.setString(5, merchantid);
                k = pstmt.executeUpdate();
                if (k > 0)
                {
                    status = "success";
                }
            }

        }
        catch (SQLException e)
        {

            logger.error("SQL Exception while updating new Fraud Merchant account_rule_mapping",e);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getUpdateInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to rule master table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {

            logger.error("System Error while updating new Fraud Merchant account_rule_mapping::",systemError);
            PZExceptionHandler.raiseDBViolationException(FraudRuleDAO.class.getName(), "getUpdateInternalAccountLevelRiskRuleList()", null, "Common", "Sql exception while connecting to rule master", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public Set getCardType()throws PZDBViolationException
    {
        Set cardSet = new TreeSet();
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {

            //con= Database.getConnection();
            con= Database.getBinConnection();
            StringBuilder query =new StringBuilder("SELECT DISTINCT TYPE FROM binbase");
            pstmt=con.prepareStatement(query.toString());

            transactionLogger.error("getCardType pstmt::::::" + pstmt);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next())
            {
                if(functions.isValueNull(rs.getString("type")))
                    cardSet.add(rs.getString("type"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting internal level risk rules",e);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getCardType()", null, "Common", "Sql exception while connecting to binbase", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting internal level risk rules::",systemError);
            PZExceptionHandler.raiseDBViolationException(ChargesDAO.class.getName(), "getCardType()", null, "Common", "Sql exception while connecting to binbase", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        return cardSet;
    }
}