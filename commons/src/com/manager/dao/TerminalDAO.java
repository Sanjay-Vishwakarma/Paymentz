package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.TerminalLimitsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TerminalDAO
{
    private static Logger logger= new Logger(TerminalDAO.class.getName());
    TransactionLogger transactionLogger= new TransactionLogger(TerminalDAO.class.getName());
    public List<TerminalVO> getTerminalsByMerchantId(String merchantId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement p=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT DISTINCT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid,mam.isActive, mam.min_transaction_amount,mam.max_transaction_amount,FROM_UNIXTIME(dtstamp) AS activationdate, gac.displayname,gt.currency FROM member_account_mapping AS mam JOIN gateway_type AS gt JOIN gateway_accounts AS gac  ON mam.accountid = gac.accountid AND gt.pgtypeid=gac.pgtypeid AND  memberid=? ORDER BY terminalid";
            p=connection.prepareStatement(selectStatement);
            p.setString(1,merchantId);
            rs=p.executeQuery();
            logger.debug("get terminals----"+p);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setDisplayName(rs.getString("displayname"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setActivationDate(rs.getString("activationdate"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getSettledTerminalList(String bankWireId, String cardtypeid, String paymodid) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        Functions functions = new Functions();
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
//            String selectStatement="SELECT toid,accountid,terminalid,paymodeid,cardtypeid FROM merchant_wiremanager WHERE settlementcycle_no=?";
            StringBuffer selectStatement= new StringBuffer("SELECT toid,accountid,terminalid,paymodeid,cardtypeid FROM merchant_wiremanager WHERE settlementcycle_no IN ("+bankWireId+")");
            if (functions.isValueNull(cardtypeid)){
                selectStatement.append(" and cardtypeid= '"+cardtypeid+"'");
            }
            if (functions.isValueNull(paymodid)){
                selectStatement.append(" and paymodeid= '"+paymodid+"'");
            }
            preparedStatement=connection.prepareStatement(selectStatement.toString());
//            preparedStatement.setString(1, bankWireId);
            logger.error("selectStatement--"+preparedStatement);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("toid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }
    public List<TerminalVO> getSettledTerminalListForAgent(String bankWireId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT toid,accountid,terminalid,paymodeid,cardtypeid,agentid FROM agent_wiremanager WHERE settlementcycle_no=?";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, bankWireId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("toid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setAgentId(rs.getString("agentid"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }
    public List<TerminalVO> getSettledTerminalListForConsolidatedAgent(String agentId,String startdate,String endDate) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            /*String selectStatement=" SELECT mam.memberid,mam.accountid,mam.terminalid,bw.bankwiremanagerId FROM member_account_mapping AS mam JOIN bank_wiremanager AS bw JOIN merchant_wiremanager \n" +
                    "AS mw WHERE mam.memberid IN (SELECT memberid FROM merchant_agent_mapping WHERE agentid = ?) AND mw.`settlementcycle_no` = bw.`bankwiremanagerId` \n" +
                    "AND mam.accountid = bw.accountid AND bw.`accountId` = mw.`accountid` AND mam.terminalid = mw.terminalid AND mam.memberid = mw.toid AND mam.accountid = mw.accountid\n" +
                    "AND bw.issettlementcronexceuted = 'Y' AND bw.isAgentCommCronExecuted = 'N' AND bw.isPayoutCronExcuted = 'Y' AND bw.bank_start_date >= ? AND bw.bank_end_date <= ? AND bw.`bankwiremanagerId` NOT IN (SELECT settlementcycle_no FROM agent_wiremanager WHERE \n" +
                    "terminalid= mw.terminalid AND markedfordeletion='N')";*/
            String selectStatement="SELECT toid,terminalid,accountid,paymodeid,cardtypeid,agentid,settlementcycle_no FROM agent_wiremanager WHERE agentid=? AND settlementstartdate>=? AND settlementstartdate<=? AND markedfordeletion='N'";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, agentId);
            preparedStatement.setString(2, startdate);
            preparedStatement.setString(3, endDate);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("toid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setAgentId(rs.getString("agentid"));
                terminalVo.setWireId(rs.getString("settlementcycle_no"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getTerminalsByAccountID(String accountId,String cardtypeid, String paymodid) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        StringBuffer selectStatement =new StringBuffer();
        Functions functions =new Functions();
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            //**vimp note :::it should be order by member id first only.
            selectStatement.append("SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,settlement_currency FROM member_account_mapping WHERE accountid= '"+accountId+"'");
            if (functions.isValueNull(cardtypeid)){
                selectStatement.append(" and cardtypeid= '"+cardtypeid+"'");
            }
            if (functions.isValueNull(paymodid)){
                selectStatement.append(" and paymodeid= '"+paymodid+"'");
            }
            selectStatement.append(" ORDER BY memberid");
            preparedStatement=connection.prepareStatement(selectStatement.toString());
//            preparedStatement.setString(1, accountId);
            rs=preparedStatement.executeQuery();
            logger.error("getTerminalsByAccountID-------------------"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setSettlementCurrency(rs.getString("settlement_currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
//            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getFilterListBasedOnInput(String agentId,String memberId,String accountId) throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList=new ArrayList();
        Connection connection=null;
        Functions functions=new Functions();
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        StringBuffer stringBuffer = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {
            connection=Database.getRDBConnection();
            stringBuffer.append("SELECT mam.memberid,accountid,paymodeid,cardtypeid,terminalid,agentid FROM member_account_mapping AS mam JOIN merchant_agent_mapping AS mm WHERE mam.memberid=mm.memberid  AND payoutActivation='Y'");

            stringBuffer.append(" and accountid=" +  ESAPI.encoder().encodeForSQL(me,accountId));
            if(functions.isValueNull(agentId)){
                stringBuffer.append(" and agentid=" +  ESAPI.encoder().encodeForSQL(me,agentId));
            }
            if(functions.isValueNull(memberId)){
                stringBuffer.append(" and mam.memberid=" +  ESAPI.encoder().encodeForSQL(me,memberId));
            }
            stringBuffer.append(" ORDER BY agentid ASC ");
            preparedStatement = connection.prepareStatement(stringBuffer.toString());
            logger.error("Query::::"+stringBuffer.toString());
            logger.error("Query::::"+preparedStatement);
            rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                TerminalVO terminalVo = new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setAgentId(rs.getString("agentid"));
                terminalVOList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVOList;
    }


    public List<TerminalVO> getTerminalsByAccountIdForAgent(String accountId) throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.memberid,accountid,paymodeid,cardtypeid,terminalid,agentid FROM member_account_mapping AS mam JOIN merchant_agent_mapping AS mm WHERE mam.memberid=mm.memberid AND accountid=? AND payoutActivation='Y'  ORDER BY agentid ASC";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, accountId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                TerminalVO terminalVo = new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setAgentId(rs.getString("agentid"));
                terminalVOList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVOList;
    }
    public List<TerminalVO> getTerminalDetailsForAgent(String agentId,String startDate,String endDate) throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="  SELECT mam.memberid,mam.accountid,mam.terminalid,mam.paymodeid,mam.cardtypeid,bw.bankwiremanagerId,bw.bank_start_date,bw.bank_end_date FROM member_account_mapping AS mam JOIN \n" +
                    "bank_wiremanager AS bw JOIN merchant_wiremanager AS mw WHERE mam.memberid IN(SELECT memberid FROM merchant_agent_mapping WHERE agentid=?) AND mw.`settlementcycle_no`=bw.`bankwiremanagerId`\n" +
                    " AND  mam.accountid=bw.accountid AND bw.`accountId`=mw.`accountid` AND mam.terminalid=mw.terminalid AND mam.memberid=mw.toid AND mam.accountid=mw.accountid\n" +
                    " AND bw.issettlementcronexceuted='Y' AND bw.isAgentCommCronExecuted='N' AND bw.isPayoutCronExcuted='Y' \n" +
                    " AND bw.bank_start_date>=? AND bw.bank_start_date<=?;";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, agentId);
            preparedStatement.setString(2, startDate);
            preparedStatement.setString(3, endDate);
            logger.error("Query getTerminalDetailsForAgent:::"+preparedStatement);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                TerminalVO terminalVo = new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setWireId(rs.getString("bankwiremanagerId"));
                terminalVo.setAgentId(agentId);
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setStartDate(rs.getString("bank_start_date"));
                terminalVo.setEndDate(rs.getString("bank_end_date"));
                terminalVOList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVOList;
    }

    public List<TerminalVO> getTerminalsByMemberAccountID(String memberId,String accountId) //sandip
    {
        List<TerminalVO> terminalVOList=new ArrayList<TerminalVO>();

        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,settlement_currency,min_payout_amount FROM member_account_mapping WHERE memberid=? and accountid=? and isActive='Y' ORDER BY terminalid";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,memberId);
            pstmt.setString(2,accountId);
            rs=pstmt.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setSettlementCurrency(rs.getString("settlement_currency"));
                terminalVo.setMinPayoutAmount(rs.getString("min_payout_amount"));
                terminalVOList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVOList;
    }

    public TerminalVO getTerminalByTerminalId(String terminalid) throws PZDBViolationException
    {
        TerminalVO terminalVo               = null;
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet                 = null;
        try
        {
            connection                  = Database.getRDBConnection();
            /*String selectStatement    = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,isActive,isCardEncryptionEnable,reject3DCard FROM member_account_mapping WHERE terminalid =?  ORDER BY terminalid";*/
            String selectStatement      = "SELECT * FROM member_account_mapping WHERE terminalid =?  ORDER BY terminalid";
            preparedStatement           = connection.prepareStatement(selectStatement);
            preparedStatement.setString(1,terminalid);
            resultSet       = preparedStatement.executeQuery();
            logger.debug("query---->"+preparedStatement);

            if (resultSet.next())
            {
                terminalVo  = new TerminalVO();

                terminalVo.setMemberId(resultSet.getString("memberid"));
                terminalVo.setTerminalId(resultSet.getString("terminalid"));
                terminalVo.setAccountId(resultSet.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(resultSet.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(resultSet.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(resultSet.getString("paymodeid")));
                terminalVo.setCardTypeId(resultSet.getString("cardtypeid"));
                terminalVo.setPaymodeId(resultSet.getString("paymodeid"));
                terminalVo.setIsActive(resultSet.getString("isActive"));
                terminalVo.setIsCardEncryptionEnable(resultSet.getString("isCardEncryptionEnable"));
                terminalVo.setReject3DCard(resultSet.getString("reject3DCard"));

                terminalVo.setDaily_amount_limit(resultSet.getString("daily_amount_limit"));
                terminalVo.setMonthly_amount_limit(resultSet.getString("monthly_amount_limit"));
                terminalVo.setDaily_card_limit(resultSet.getString("daily_card_limit"));
                terminalVo.setWeekly_card_limit(resultSet.getString("weekly_card_limit"));
                terminalVo.setMonthly_card_limit(resultSet.getString("monthly_card_limit"));
                terminalVo.setDaily_card_amount_limit(resultSet.getString("daily_card_amount_limit"));
                terminalVo.setWeekly_card_amount_limit(resultSet.getString("weekly_card_amount_limit"));
                terminalVo.setMonthly_card_amount_limit(resultSet.getString("monthly_card_amount_limit"));
                terminalVo.setMin_trans_amount(resultSet.getString("min_transaction_amount"));
                terminalVo.setMax_trans_amount(resultSet.getString("max_transaction_amount"));
                terminalVo.setPriority(resultSet.getString("priority"));
                terminalVo.setIsTest(resultSet.getString("isTest"));
                terminalVo.setWeekly_amount_limit(resultSet.getString("weekly_amount_limit"));
                terminalVo.setIsRecurring(resultSet.getString("is_recurring"));
                terminalVo.setIsRestrictedTicketActive(resultSet.getString("isRestrictedTicketActive"));
                terminalVo.setIsTokenizationActive(resultSet.getString("isTokenizationActive"));
                terminalVo.setAddressDetails(resultSet.getString("addressDetails"));
                terminalVo.setAddressValidation(resultSet.getString("addressValidation"));
                terminalVo.setCardDetailRequired(resultSet.getString("cardDetailRequired"));
                terminalVo.setIsPSTTerminal(resultSet.getString("isPSTTerminal"));
                terminalVo.setIsManualRecurring(resultSet.getString("isManualRecurring"));
                terminalVo.setRiskRuleActivation(resultSet.getString("riskruleactivation"));
                terminalVo.setDaily_avg_ticket(resultSet.getString("daily_avg_ticket"));
                terminalVo.setWeekly_avg_ticket(resultSet.getString("weekly_avg_ticket"));
                terminalVo.setMonthly_avg_ticket(resultSet.getString("monthly_avg_ticket"));
                terminalVo.setSettlementCurrency(resultSet.getString("settlement_currency"));
                terminalVo.setMinPayoutAmount(resultSet.getString("min_payout_amount"));
                terminalVo.setPayoutActivation(resultSet.getString("payoutActivation"));
                terminalVo.setAutoRedirectRequest(resultSet.getString("autoRedirectRequest"));
                terminalVo.setIsCardWhitelisted(resultSet.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(resultSet.getString("isEmailWhitelisted"));
                terminalVo.setCurrencyConversion(resultSet.getString("currency_conversion"));
                terminalVo.setConversionCurrency(resultSet.getString("conversion_currency"));
                terminalVo.setIsbin_routing(resultSet.getString("binRouting"));
                terminalVo.setIsEmi_support(resultSet.getString("emi_support"));
                terminalVo.setWhitelisting(resultSet.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(resultSet.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(resultSet.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(resultSet.getString("amountLimitCheck"));
                terminalVo.setActionExecutorId(resultSet.getString("actionExecutorId"));
                terminalVo.setActionExecutorName(resultSet.getString("actionExecutorName"));
                terminalVo.setProcessor_partnerid(resultSet.getString("processor_partnerid"));


            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByTerminalId()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByTerminalId()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getActiveInActiveTerminalInfo(String terminalId) throws PZDBViolationException
    {
        TerminalVO terminalVo=new TerminalVO();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,gt.currency, FROM_UNIXTIME(mam.dtstamp) AS activationdate FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE terminalid = ? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, terminalId);
            rs=preparedStatement.executeQuery();
            if (rs.next())
            {
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setActivationDate(rs.getString("activationdate"));
                terminalVo.setCurrency(rs.getString("currency"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getActiveInActiveTerminalInfo()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getActiveInActiveTerminalInfo()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getMemberTerminalInfo(String memberId,String terminalid) throws PZDBViolationException   //sandip
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid FROM member_account_mapping WHERE terminalid =? and memberid=? ";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,terminalid);
            pstmt.setString(2,memberId);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
            }
            logger.debug("agent query for terminal details----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }


    public TerminalVO getTerminalMerchantsInFo(String merchantId , String terminalid) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement p=null;
        ResultSet rs=null;
        TerminalVO terminalVo = null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT  mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid,mam.isActive, mam.min_transaction_amount,mam.max_transaction_amount," +
                    " gac.displayname,gt.currency,gac.partnerid ,gac.merchantid,gt.gateway FROM member_account_mapping AS mam JOIN gateway_type AS gt JOIN gateway_accounts AS gac " +
                    " ON mam.accountid = gac.accountid AND gt.pgtypeid=gac.pgtypeid AND memberid=? AND terminalid=? ORDER BY terminalid";
            p=connection.prepareStatement(selectStatement);
            p.setString(1,merchantId);
            p.setString(2,terminalid);
            rs=p.executeQuery();
            System.out.println(p);
            while (rs.next())
            {
                 terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setDisplayName(rs.getString("displayname"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setPartnerId(rs.getString("partnerid"));
                terminalVo.setGwMid(rs.getString("merchantid"));
                terminalVo.setGateway(rs.getString("gateway"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public boolean isChargesMappedWithTerminal(String terminalId) throws PZDBViolationException
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT * FROM member_accounts_charges_mapping WHERE terminalid=?";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, terminalId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                return true;
            }
            else
                return false ;
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"isChargesMappedWithTerminal()",null,"Common","Sql exception connecting to member_account_charges_mapping table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"isChargesMappedWithTerminal()",null,"Common","Sql exception due to incorrect query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return false;
    }

    public HashMap<String,String> getGatewayHash(TerminalVO terminalVO) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        HashMap<String,String> gatewayhash = new HashMap<String, String>();
        if(terminalVO.getAccountId()==null || terminalVO.getAccountId().equals("") || terminalVO.getAccountId().equals("null"))
        {
            try
            {
                con=Database.getRDBConnection();
                String query= "select accountid from member_account_mapping where memberid=?";
                pstmt2= con.prepareStatement(query);
                pstmt2.setString(1,terminalVO.getMemberId());
                rs2 = pstmt2.executeQuery();
                while (rs2.next())
                {
                    String gateway = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getGateway();
                    String displayname = GatewayAccountService.getGatewayAccount(rs2.getString("accountid")).getDisplayName();
                    gatewayhash.put(gateway,displayname);
                }
            }
            catch (SQLException se)
            {
                logger.error("SQLException is occure",se);
                PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getGatewayHash()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,se.getMessage(),se.getCause());
            }
            catch (SystemError systemError)
            {
                logger.error("SQL Exception while collect getGatewayHAsh",systemError);
                PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getGatewayHash()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
            }
            finally {
                Database.closeResultSet(rs2);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(con);
            }
        }
        return gatewayhash;
    }

    public Set<String> getAccountIds(String memberId) throws SystemError
    {
        Set<String> accountIds = new HashSet<String>();
        StringBuffer query = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        try
        {
            conn = Database.getRDBConnection();
            query = new StringBuffer("SELECT accountid FROM member_account_mapping WHERE memberid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                accountIds.add(rs.getString("accountid"));
            }
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return accountIds;
    }

    public String getMemberTerminalId(String memberId,String accountId,String payModeId,String cardTypeId)  //sandip
    {
        String terminalId=null;
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rsPayout=null;
        try
        {
            con= Database.getRDBConnection();
            String Query = "SELECT terminalid FROM member_account_mapping WHERE memberId=? AND accountid=? And paymodeid=? And cardtypeid=?";
            preparedStatement=con.prepareStatement(Query);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,accountId);
            preparedStatement.setString(3,payModeId);
            preparedStatement.setString(4,cardTypeId);
            rsPayout=preparedStatement.executeQuery();
            if(rsPayout.next())
            {
                terminalId=rsPayout.getString("terminalid");
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
            Database.closeResultSet(rsPayout);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return terminalId;
    }

    //getting terminals according to the agentId
    public List<TerminalVO> getTerminalsByAgentId(String agentId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid FROM member_account_mapping as mam JOIN members as m on m.memberid=mam.memberid  WHERE m.agentId=? and mam.isActive='Y' ORDER BY mam.terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, agentId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAgentId()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAgentId()", null, "Common", "Sql exception  due to incorrect query to member_account_mapping and members", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getTerminalsByPartnerId(String partnerId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  WHERE m.partnerId=? ORDER BY mam.terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, partnerId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCompany_name(rs.getString("company_name"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByPartnerId()",null,"Common","System error while Connecting to member_account_mapping and members",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByPartnerId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping and members",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getActiveTerminalsByPartnerId(String partnerId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  WHERE m.partnerId=? and mam.isActive='Y' ORDER BY mam.terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, partnerId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCompany_name(rs.getString("company_name"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","System error while Connecting to member_account_mapping and members",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping and members",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public boolean isTokenizationActiveOnTerminal(String memberId,String terminalid) throws PZDBViolationException
    {
        Connection connection=null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        boolean tokenizationActive=false;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="select terminalid from member_account_mapping where memberid=? and terminalid=? and isTokenizationActive=?";
            pstmt=connection.prepareStatement(selectStatement);
            pstmt.setString(1, memberId);
            pstmt.setString(2, terminalid);
            pstmt.setString(3,"Y");
            transactionLogger.error("isTokenizationActiveOnTerminal------------>"+pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                tokenizationActive=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect isTokenizationActiveOnTerminal",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"isTokenizationActiveOnTerminal()",null,"Common","System error while Connecting to isTokenizationActiveOnTerminal",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect isTokenizationActiveOnTerminal",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"isTokenizationActiveOnTerminal()",null,"Common","Sql exception  due to incorrect query to isTokenizationActiveOnTerminal",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return tokenizationActive;
    }

    public TerminalVO getMemberTerminalfromMemberAndTerminal(String memberId,String terminalid,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query=null;
        Functions functions=new Functions();
        try
        {
            connection=Database.getRDBConnection();
            query = new StringBuffer("SELECT gt.currency,memberid,mam.accountid,paymodeid,cardtypeid,terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,addressDetails,mam.addressValidation,cardDetailRequired,mam.is_recurring,isManualRecurring,isPSTTerminal,isTokenizationActive, payoutActivation, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE terminalid=? AND memberid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL'");
            if(functions.isValueNull(accountId))
                query.append(" AND mam.accountid=?");
            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1,terminalid);
            pstmt.setString(2, memberId);
            if(functions.isValueNull(accountId))
                pstmt.setString(3,accountId);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setPayoutActivation(rs.getString("payoutActivation"));
                terminalVo.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
            }
            logger.debug("query for token with merchant----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getMemberTerminalfromMemberAndTerminalAndCurrency(String memberId,String terminalid, String currency,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query=null;
        Functions functions=new Functions();
        try
        {
            connection=Database.getRDBConnection();
            query = new StringBuffer("SELECT gt.currency,gt.gateway,memberid,mam.accountid,paymodeid,cardtypeid,terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,addressDetails,mam.addressValidation,cardDetailRequired,mam.is_recurring,isManualRecurring,isPSTTerminal,isTokenizationActive, payoutActivation, mam.autoRedirectRequest,mam.reject3DCard, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.currency_conversion,mam.conversion_currency,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE terminalid=? AND memberid=? AND gt.currency = ? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                query.append(" AND mam.accountid=?");
            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1,terminalid);
            pstmt.setString(2, memberId);
            pstmt.setString(3, currency);
            if(functions.isValueNull(accountId))
                pstmt.setString(4,accountId);
            transactionLogger.error("getMemberTerminalfromMemberAndTerminalAndCurrency-->"+pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setPayoutActivation(rs.getString("payoutActivation"));
                terminalVo.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
                terminalVo.setGateway(rs.getString("gateway"));
            }
            logger.debug("query for token with merchant----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getMemberTerminalfromTerminal(String terminalid) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,isActive,max_transaction_amount,min_transaction_amount,currency_conversion,conversion_currency,isCardWhitelisted, " +
                    "isEmailWhitelisted,cardDetailRequired,isRestrictedTicketActive,isManualRecurring,is_recurring,addressDetails,addressValidation,isPSTTerminal,reject3DCard,whitelisting_details,cardLimitCheck,cardAmountLimitCheck,amountLimitCheck FROM member_account_mapping WHERE terminalid =?";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,terminalid);
            logger.debug("qury-->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setIsRestrictedTicketActive(rs.getString("isRestrictedTicketActive"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getMemberTerminalfromTerminal(String terminalid, String memberId,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query=null;
        Functions functions=new Functions();
        try
        {
            connection=Database.getRDBConnection();
            query=new StringBuffer("SELECT mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.currency_conversion, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.cardDetailRequired,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc FROM member_account_mapping as mam,gateway_accounts as ga WHERE mam.accountid=ga.accountid and terminalid =? and memberid=?");
            if(functions.isValueNull(accountId))
                query.append(" AND mam.accountid=?");
            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1,terminalid);
            pstmt.setString(2,memberId);
            if(functions.isValueNull(accountId))
                pstmt.setString(3,accountId);
            logger.debug("qury-->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public boolean isMemberMappedWithTerminal(String memberId,String terminalId)throws SQLException,SystemError
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean b1=false;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select memberid from member_account_mapping where memberid=? and terminalid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,memberId);
            pstmt.setString(2, terminalId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                b1=true;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b1;
    }

    public List<TerminalVO> getTerminalListByAgent(String agentId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,ma.memberid, gt.currency,mam.isActive FROM member_account_mapping AS mam, members AS m, merchant_agent_mapping AS ma, gateway_type AS gt, gateway_accounts AS ga WHERE ma.memberid=m.memberid AND m.memberid=mam.memberid AND ma.memberid=mam.memberid  AND ma.agentId=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ORDER BY terminalid ASC";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, agentId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));

                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setIsActive(rs.getString("isActive"));
                accountList.add(terminalVo);
            }
            logger.debug("query agent -----"+preparedStatement);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalListByAgent()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalListByAgent()", null, "Common", "Sql exception  due to incorrect query to member_account_mapping and members", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public Hashtable<String,String> getMemberTerminalList() throws PZDBViolationException
    {
        Hashtable<String, String> terminalid = new Hashtable<String, String>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid, mam.terminalid, mam.memberid, gt.currency, mam.isActive FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE ga.pgtypeid=gt.pgtypeid AND mam.accountid=ga.accountid ORDER BY terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                terminalid.put(rs.getString("terminalid"), rs.getString("currency"));
            }
            logger.debug("query  -----"+preparedStatement);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalList()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalList()", null, "Common", "Sql exception  due to incorrect query to member_account_mapping and members", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalid;
    }

    public String getTerminalByAgentId(String agentId) throws PZDBViolationException
    {
        Connection connection=null;
        String terminalids = null;
        StringBuffer terminal = new StringBuffer();
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            StringBuffer selectStatement = new StringBuffer();
            selectStatement.append("SELECT ma.terminalid FROM member_account_mapping AS ma INNER JOIN merchant_agent_mapping AS mm ON mm.memberid= ma.memberid WHERE isActive='Y' AND mm.agentId=?");
            preparedStatement=connection.prepareStatement(selectStatement.toString());
            preparedStatement.setString(1, agentId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                terminal.append(rs.getString("terminalid")+",");
            }
            if(terminal.length() > 0)
            {
                terminalids = terminal.toString().substring(0,terminal.length()-1);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByAgentId()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByAgentId()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalids;
    }

    public String getTerminalByMemberIdAndAgentId(String memberid,String agentId) throws PZDBViolationException
    {
        Connection connection=null;
        String terminalids = null;
        StringBuffer terminal = new StringBuffer();
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            StringBuffer selectStatement = new StringBuffer();
            selectStatement.append("SELECT ma.terminalid FROM member_account_mapping AS ma INNER JOIN merchant_agent_mapping AS mm ON mm.memberid= ma.memberid WHERE isActive='Y' AND mm.memberid=? AND mm.agentid=?");
            preparedStatement=connection.prepareStatement(selectStatement.toString());
            preparedStatement.setString(1, memberid);
            preparedStatement.setString(2, agentId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                terminal.append(rs.getString("terminalid") + ",");
            }
            if(terminal.length() > 0)
            {
                terminalids = terminal.toString().substring(0,terminal.length()-1);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByMemberIdAndAgentId()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByMemberIdAndAgentId()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalids;
    }

    public Map<String,List<TerminalVO>> getTerminalsByMemberIdForPartner(String partnerId) throws PZDBViolationException
    {
        Map<String,List<TerminalVO>> accountMap= new HashMap<String, List<TerminalVO>>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.addressValidation,m.memberid FROM member_account_mapping as mam JOIN members as m on m.memberid=mam.memberid  WHERE m.partnerId=? and mam.isActive='Y' ORDER BY mam.terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, partnerId);
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                if(accountMap.containsKey(terminalVo.getMemberId()))
                {
                    List<TerminalVO> terminalVOList = null;
                    if(accountMap.get(terminalVo.getMemberId())!=null)
                    {
                        terminalVOList=accountMap.get(terminalVo.getMemberId());
                        terminalVOList.add(terminalVo);
                    }
                    else
                    {
                        terminalVOList=new ArrayList<TerminalVO>();
                        terminalVOList.add(terminalVo);
                        accountMap.put(terminalVo.getMemberId(),terminalVOList);
                    }
                }
                else
                {
                    List<TerminalVO> terminalVOList = new ArrayList<TerminalVO>();
                    terminalVOList.add(terminalVo);
                    accountMap.put(terminalVo.getMemberId(),terminalVOList);
                    accountMap.put(terminalVo.getMemberId(),terminalVOList);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMemberIdForPartner()",null,"Common","System error while Connecting to member_account_mapping and members",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMemberIdForPartner()",null,"Common","Sql exception  due to incorrect query to member_account_mapping and members",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountMap;
    }

    //MODIFICATION IN QUERY AND PARAMETER AS PER SUPERPARTNER ACCESS.
    public List<TerminalVO> getTerminalsByMemberIdAndPartner(String partnerId, String memberid) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement = "";
            if(memberid==null || "".equals(memberid))
            {
                selectStatement = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid JOIN partners AS p ON m.partnerid = p.partnerid WHERE (m.partnerId=? OR p.superadminid=? ) and mam.isActive='Y' ORDER BY mam.terminalid";
                preparedStatement = connection.prepareStatement(selectStatement);
                preparedStatement.setString(1, partnerId);
                preparedStatement.setString(2, partnerId);
            }
            else
            {
                selectStatement = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  JOIN partners AS p ON m.partnerid = p.partnerid WHERE (m.partnerId=? OR p.superadminid=? )  and m.memberid = ? and mam.isActive='Y' ORDER BY mam.terminalid";
                preparedStatement = connection.prepareStatement(selectStatement);
                preparedStatement.setString(1, partnerId);
                preparedStatement.setString(2, partnerId);
                preparedStatement.setString(3, memberid);
            }
            rs=preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCompany_name(rs.getString("company_name"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","System error while Connecting to member_account_mapping and members",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping and members",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    //MODIFICATION IN QUERY AS PER SUPERPARTNER ACCESS.
    public List<TerminalVO> getAllTerminalsByMemberId(String partnerId, String memberid) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement = "";
            if(memberid==null || "".equals(memberid))
            {
                selectStatement = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  JOIN partners AS p ON m.partnerid = p.partnerid WHERE (m.partnerId=? OR p.superadminid=? ) ORDER BY mam.terminalid";
                preparedStatement = connection.prepareStatement(selectStatement);
                preparedStatement.setString(1, partnerId);
                preparedStatement.setString(2, partnerId);
            }
            else
            {
                selectStatement = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  JOIN partners AS p ON m.partnerid = p.partnerid WHERE (m.partnerId=? OR p.superadminid=? ) and m.memberid = ? ORDER BY mam.terminalid";
                preparedStatement = connection.prepareStatement(selectStatement);
                preparedStatement.setString(1, partnerId);
                preparedStatement.setString(2, partnerId);
                preparedStatement.setString(3, memberid);
            }
            rs = preparedStatement.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCompany_name(rs.getString("company_name"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
            logger.debug("accountList inside try:::::"+accountList);

        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","System error while Connecting to member_account_mapping and members",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getActiveTerminalsByPartnerId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping and members",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        logger.debug("accountList:::::"+accountList);
        return accountList;
    }

    public List<TerminalVO> getMemberandUserTerminalList(String merchantId,String role) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            //connection=Database.getConnection();
            connection=Database.getRDBConnection();
            String selectStatement = "";
            if(role.equalsIgnoreCase("submerchant"))
            {
                selectStatement = "SELECT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid,ma.isActive, gac.displayname, ga.currency FROM member_user_account_mapping AS mam JOIN member_account_mapping AS ma JOIN gateway_accounts AS gac JOIN gateway_type AS ga ON mam.accountid = gac.accountid AND gac.pgtypeid = ga.pgtypeid AND mam.terminalid = ma.terminalid AND mam.userid=? ORDER BY terminalid";
            }
            else
            {
                selectStatement = "SELECT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid, gac.displayname,mam.isActive, ga.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gac JOIN gateway_type AS ga ON mam.accountid = gac.accountid AND gac.pgtypeid = ga.pgtypeid AND memberid=? ORDER BY terminalid";
            }
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, merchantId);
            rs=preparedStatement.executeQuery();
            logger.debug("terminal query----"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setDisplayName(rs.getString("displayname"));
                terminalVo.setCurrency((rs.getString("currency")));
                terminalVo.setIsActive(rs.getString("isActive"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getTerminalsByMerchantId(String merchantId,String role) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement = "";
            if (role.equalsIgnoreCase("submerchant"))
            {
                selectStatement="SELECT DISTINCT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid,mam.isActive, mam.min_transaction_amount,mam.max_transaction_amount,FROM_UNIXTIME(dtstamp) AS activationdate, gac.displayname,gt.currency FROM member_account_mapping AS mam JOIN member_user_account_mapping AS muam JOIN gateway_type AS gt JOIN gateway_accounts AS gac  ON mam.accountid = gac.accountid AND gt.pgtypeid=gac.pgtypeid AND mam.terminalid = muam.terminalid AND muam.userid=?ORDER BY terminalid";
            }
            else
            {
                selectStatement="SELECT DISTINCT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid,mam.isActive, mam.min_transaction_amount,mam.max_transaction_amount,FROM_UNIXTIME(dtstamp) AS activationdate, gac.displayname,gt.currency FROM member_account_mapping AS mam JOIN gateway_type AS gt JOIN gateway_accounts AS gac  ON mam.accountid = gac.accountid AND gt.pgtypeid=gac.pgtypeid AND mam.terminalid AND mam.memberid=? ORDER BY terminalid";
            }
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, merchantId);
            rs=preparedStatement.executeQuery();
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setDisplayName(rs.getString("displayname"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setActivationDate(rs.getString("activationdate"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO>  getMemberandUserCurrencyList(String merchantId,String role) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement = "SELECT mam.accountid, mam.paymodeid, mam.cardtypeid, mam.terminalid, gac.displayname,mam.isActive, ga.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gac JOIN gateway_type AS ga ON mam.accountid = gac.accountid AND gac.pgtypeid = ga.pgtypeid AND memberid=?  GROUP BY currency";
            PreparedStatement p=connection.prepareStatement(selectStatement);
            p.setString(1,merchantId);
            ResultSet rs=p.executeQuery();
            logger.debug("terminal query----"+p);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setDisplayName(rs.getString("displayname"));
                terminalVo.setCurrency((rs.getString("currency")));
                terminalVo.setIsActive(rs.getString("isActive"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getMemberandTerminalList(String merchantid, String currency) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String query = "";
            if(currency.equals(null) || currency.equals(""))
            {
                query = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  WHERE m.memberid = ? ORDER BY mam.terminalid";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1, merchantid);
            }
            else
            {
                query = "SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,m.memberid,mam.isActive,m.company_name,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid JOIN members AS m ON m.memberid=mam.memberid  WHERE m.memberid = ? AND gtype.currency = ? ORDER BY mam.terminalid";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1, merchantid);
                preparedStatement.setString(2, currency);
            }
            rs=preparedStatement.executeQuery();
            logger.debug("terminal query----"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setCurrency((rs.getString("currency")));
                terminalVo.setIsActive(rs.getString("isActive"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }
    public List<TerminalVO> getCurrencyList(String merchantId,String role) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList<TerminalVO>();
        Connection connection=null;
        PreparedStatement preparedStatement = null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement = "";
            if(role.equalsIgnoreCase("submerchant"))
            {
                selectStatement = "SELECT ga.currency FROM member_user_account_mapping AS mam JOIN member_account_mapping AS ma JOIN gateway_accounts AS gac JOIN gateway_type AS ga ON mam.accountid = gac.accountid AND gac.pgtypeid = ga.pgtypeid AND mam.terminalid = ma.terminalid AND mam.userid=? GROUP BY ga.currency";
            }
            else
            {
                selectStatement = "SELECT ga.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gac JOIN gateway_type AS ga ON mam.accountid = gac.accountid AND gac.pgtypeid = ga.pgtypeid AND memberid=? GROUP BY ga.currency";
            }
            preparedStatement = connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, merchantId);
            rs=preparedStatement.executeQuery();
            logger.debug("currency query----"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(merchantId);
                terminalVo.setCurrency((rs.getString("currency")));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalsByMerchantId()",null,"Common","Sql exception  due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public TerminalVO getCardIdAndPaymodeIdFromTerminal(String memberId, String terminalId, String currency,String paymodeId, String cardTypeId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            //String qry = "select mam.paymodeid, mam.cardtypeid,pb.transactionType,mam.cardDetailRequired,mam.addressValidation,mam.accountid FROM member_account_mapping AS mam,payment_brand AS pb WHERE mam.memberid=? AND mam.terminalid=? AND mam.paymodeid=pb.paymodeid";
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid,mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.terminalid=? AND gt.currency=? AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,terminalId);
            ps.setString(3,currency);
            ps.setString(4, paymodeId);
            ps.setString(5,cardTypeId);
            logger.debug("query from getCardIdAndPaymodeIdFromTerminal()--->" + ps);
            rs = ps.executeQuery();
            if(rs.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVO.setAddressValidation(rs.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVO.setMemberId(rs.getString("merchantid"));
                terminalVO.setGateway(rs.getString("gateway"));
                terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVO.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromTerminal()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromTerminal()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;

    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid,mam.emi_support FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='N' ORDER BY mam.priority,mam.terminalid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid,mam.emi_support FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='N'");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountId=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            if(functions.isValueNull(accountId))
                ps.setString(5,accountId);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String currency,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            if(functions.isValueNull(accountId))
                ps.setString(3,accountId);
            logger.debug("getPaymdeCardTerminalVO ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getMultipleCurrencyPaymdeCardTerminalVO(String memberId,String currency1,String currency2,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.emi_support,mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency IN (?,?) AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency1);
            ps.setString(3,currency2);
            if(functions.isValueNull(accountId))
                ps.setString(4,accountId);
            logger.debug("getPaymdeCardTerminalVO ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    if(currency1.equalsIgnoreCase(rs.getString("currency")))
                        terminalVO.setCurrency(currency1);
                    else
                        terminalVO.setCurrency(currency2);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency(),terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String currency,String paymodeId,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency=? AND mam.paymodeid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,paymodeId);
            if(functions.isValueNull(accountId))
                ps.setString(4,accountId);
            logger.debug("getPaymdeCardTerminalVO ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency))
                         terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String paymodeId,String currency,String currency1,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency IN (?,?) AND mam.paymodeid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,currency1);
            ps.setString(4,paymodeId);
            if(functions.isValueNull(accountId))
                ps.setString(5,accountId);
            logger.debug("getPaymdeCardTerminalVO ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVOfromPaymodeCardId(String memberId,String currency,String paymodeId,String cardId,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency=? AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,paymodeId);
            ps.setString(4,cardId);
            if(functions.isValueNull(accountId))
                ps.setString(5,accountId);
            logger.debug("getPaymdeCardTerminalVOfromPaymodeCardId ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVOfromPaymodeCardId(String memberId,String paymodeId,String cardId,String currency,String currency1,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency in (?,?) AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            qry.append(" ORDER BY mam.priority,mam.terminalid");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,currency1);
            ps.setString(4,paymodeId);
            ps.setString(5,cardId);
            if(functions.isValueNull(accountId))
                ps.setString(6,accountId);
            logger.debug("getPaymdeCardTerminalVOfromPaymodeCardId ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVOfromTerminalID(String memberId,String currency,String terminalId,String accountId) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency=? AND mam.terminalid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,terminalId);
            if(functions.isValueNull(accountId))
                ps.setString(4,accountId);
            logger.debug("getPaymdeCardTerminalVOfromTerminalID ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                    //terminalHash.put(rs.getString("paymodeid")+"-"+rs.getString("cardtypeid")+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public HashMap getPaymdeCardTerminalVOfromTerminalID(String memberId,String currency,String currency1,String terminalId,String accountId) throws PZDBViolationException
    {
        logger.debug("inside getPaymdeCardTerminalVOfromTerminalID");
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions=new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency in(?,?) AND mam.terminalid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ");
            if(functions.isValueNull(accountId))
                qry.append(" AND mam.accountid=?");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,currency);
            ps.setString(3,currency1);
            ps.setString(4,terminalId);
            if(functions.isValueNull(accountId))
                ps.setString(5,accountId);
            logger.debug("getPaymdeCardTerminalVOfromTerminalID ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+currency,terminalVO);
                    //terminalHash.put(rs.getString("paymodeid")+"-"+rs.getString("cardtypeid")+"-"+currency,terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }

    public TerminalVO getTerminalDetailsFromPaymentBrandWithPartnerId(String partnerId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        Functions functions = new Functions();
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT m.partnerid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.isTokenizationActive,mam.isActive,mam.accountid FROM member_account_mapping AS mam,gateway_accounts AS ga,members AS m, gateway_type AS gt WHERE m.partnerid=? AND mam.memberid=m.memberid AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.isActive='Y'";
            ps = con.prepareStatement(qry);
            ps.setString(1,partnerId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4,currency);
            logger.debug("queryy----"+ps);
            rs = ps.executeQuery();
            if(rs.next())
            {
                terminalVO = new TerminalVO();

                terminalVO.setPartnerId(rs.getString("partnerid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVO.setIsActive(rs.getString("isActive"));
                terminalVO.setCurrency(currency);
            }
            logger.debug("query for partner token without terminalid----"+ps);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public TerminalVO getTerminalFromPaymodeCardtypeMemberidCurrency(String memberId, String paymentType, String cardType, String currency)
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,mam.accountid,ga.pgtypeid,mam.paymodeid,mam.cardtypeid,gt.currency,mam.addressDetails,mam.addressValidation,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,ga.merchantid,gt.gateway,mam.isTokenizationActive,mam.isActive FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt  WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,memberId);
            pstmt.setString(2,paymentType);
            pstmt.setString(3,cardType);
            pstmt.setString(4,currency);
            logger.debug("Query---->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVO.setAddressDetails(rs.getString("addressDetails"));
                terminalVO.setAddressValidation(rs.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVO.setMemberId(rs.getString("merchantid"));
                terminalVO.setGateway(rs.getString("gateway"));
                terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVO.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException", e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public List<TerminalVO> getAllNewTerminals()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOs=new ArrayList();
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            while(res.next())
            {
                String createDate=res.getString("registrationon");
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)),targetFormat.format(new Date()));
                if(dy<=90)
                {
                    terminalVO=new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setActivationDate(res.getString("registrationon"));
                    terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                    terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                    terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                    terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                    terminalVO.setActivationDate(res.getString("registrationon"));
                    terminalVOs.add(terminalVO);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  terminalVOs;
    }

    public List<TerminalVO> getAllOldTerminals()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOs=new ArrayList();
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            while(res.next())
            {
                String createDate=res.getString("registrationon");
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)),targetFormat.format(new Date()));
                if(dy>90)
                {
                    terminalVO=new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setActivationDate(res.getString("registrationon"));
                    terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                    terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                    terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                    terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                    terminalVOs.add(terminalVO);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getNewAllTerminals()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getNewAllTerminals()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  terminalVOs;
    }

    public List<TerminalVO> getAllTerminals()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOs=new ArrayList();
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while(res.next())
            {
                terminalVO=new TerminalVO();
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(res.getString("accountid"));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setActivationDate(res.getString("registrationon"));
                terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                terminalVOs.add(terminalVO);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewAllTerminals()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  terminalVOs;
    }

    public Map<String,List<TerminalVO>> getAllTerminalsGroupByMerchant()throws PZDBViolationException
    {

        Map<String,List<TerminalVO>> stringListMap =new HashMap();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE isActive='Y' and dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while(res.next())
            {
                terminalVO=new TerminalVO();

                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(res.getString("accountid"));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setActivationDate(res.getString("registrationon"));
                terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                terminalVOs =stringListMap.get(res.getString("memberid"));
                if(terminalVOs==null)
                {
                    terminalVOs =new ArrayList();
                    terminalVOs.add(terminalVO);
                }
                else
                {
                    terminalVOs.add(terminalVO);
                }
                stringListMap.put(res.getString("memberid"),terminalVOs);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAllTerminalsGroupByMerchant()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public Map<String,List<TerminalVO>> getAuthCaptureModeAllTerminalsGroupByMerchant()throws PZDBViolationException
    {
        Map<String,List<TerminalVO>> stringListMap =new HashMap();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.inactive_period_threshold,mam.first_submission_threshold,FROM_UNIXTIME(mam.dtstamp) AS registrationon FROM member_account_mapping AS mam JOIN members m ON mam.memberid=m.memberid AND m.isservice='N' AND mam.dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while(res.next())
            {
                terminalVO=new TerminalVO();
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(res.getString("accountid"));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setActivationDate(res.getString("registrationon"));
                terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                terminalVOs =stringListMap.get(res.getString("memberid"));
                if(terminalVOs==null)
                {
                    terminalVOs =new ArrayList();
                    terminalVOs.add(terminalVO);
                }
                else
                {
                    terminalVOs.add(terminalVO);
                }
                stringListMap.put(res.getString("memberid"), terminalVOs);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAuthCaptureModeAllTerminalsGroupByMerchant()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAuthCaptureModeAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAuthCaptureModeAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public Map<String,List<TerminalVO>> getNewTerminalsGroupByMerchant()throws PZDBViolationException
    {
        Map<String,List<TerminalVO>> stringListMap =new HashMap();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            while(res.next())
            {
                String createDate=res.getString("registrationon");
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)),targetFormat.format(new Date()));
                if(dy<=90)
                {
                    terminalVO=new TerminalVO();
                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setActivationDate(res.getString("registrationon"));
                    terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                    terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                    terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                    terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));
                    terminalVOs =stringListMap.get(res.getString("memberid"));
                    if(terminalVOs==null)
                    {
                        terminalVOs =new ArrayList();
                        terminalVOs.add(terminalVO);
                    }
                    else
                    {
                        terminalVOs.add(terminalVO);
                    }
                    stringListMap.put(res.getString("memberid"),terminalVOs);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewTerminalsGroupByMerchant()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getNewTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public Map<String,List<TerminalVO>> getOldTerminalsGroupByMerchant()throws PZDBViolationException
    {

        Map<String,List<TerminalVO>> stringListMap =new HashMap();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,inactive_period_threshold,first_submission_threshold,FROM_UNIXTIME(dtstamp) as registrationon FROM member_account_mapping WHERE isActive='Y' and dtstamp IS NOT NULL";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            while(res.next())
            {
                String createDate=res.getString("registrationon");
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(createDate)),targetFormat.format(new Date()));
                if(dy>90)
                {
                    terminalVO=new TerminalVO();

                    terminalVO.setMemberId(res.getString("memberid"));
                    terminalVO.setAccountId(res.getString("accountid"));
                    terminalVO.setPaymodeId(res.getString("paymodeid"));
                    terminalVO.setCardTypeId(res.getString("cardtypeid"));
                    terminalVO.setTerminalId(res.getString("terminalid"));
                    terminalVO.setActivationDate(res.getString("registrationon"));
                    terminalVO.setFirstSubmissionAllowed(res.getString("first_submission_threshold"));
                    terminalVO.setInactivePeriodAllowed(res.getString("inactive_period_threshold"));
                    terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                    terminalVO.setPaymentTypeName(GatewayAccountService.getPaymentTypes(res.getString("paymodeid")));

                    terminalVOs =stringListMap.get(res.getString("memberid"));
                    if(terminalVOs==null)
                    {
                        terminalVOs =new ArrayList();
                        terminalVOs.add(terminalVO);

                    }
                    else
                    {
                        terminalVOs.add(terminalVO);
                    }
                    stringListMap.put(res.getString("memberid"),terminalVOs);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getOldTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getOldTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getOldTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public boolean doInactiveTerminal(String terminalId)
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean b1=false;
        try
        {
            con=Database.getConnection();
            StringBuffer sb=new StringBuffer("update member_account_mapping set isActive='N' where terminalid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,terminalId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                b1=true;
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
        }
        catch (Exception e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b1;
    }

    public TerminalVO getMemberTerminalWithActivationDetails(String memberId, String terminalid) throws PZDBViolationException
    {
        TerminalVO terminalVo = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            query = "SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,isActive,max_transaction_amount,min_transaction_amount,addressDetails,addressValidation,cardDetailRequired,is_recurring,isManualRecurring,isPSTTerminal,FROM_UNIXTIME(dtstamp) as activationdate FROM member_account_mapping WHERE terminalid =? and memberid=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalid);
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo = new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setActivationDate(rs.getString("activationdate"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalWithActivationDetails()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalWithActivationDetails()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalLimitsVO getMemberTerminalProcessingLimitVO(String memberId, String terminalId) throws PZDBViolationException
    {
        TerminalLimitsVO terminalLimitsVO = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            query = "SELECT daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,weekly_amount_limit,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket FROM member_account_mapping WHERE terminalid =? and memberid=? ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, terminalId);
            pstmt.setString(2, memberId);
            logger.debug("query:::::::"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                terminalLimitsVO = new TerminalLimitsVO();
                terminalLimitsVO.setDailyCardLimit(rs.getDouble("daily_card_limit"));
                terminalLimitsVO.setWeeklyCardLimit(rs.getDouble("weekly_card_limit"));
                terminalLimitsVO.setMonthlyCardLimit(rs.getDouble("monthly_card_limit"));
                terminalLimitsVO.setDailyAmountLimit(rs.getDouble("daily_amount_limit"));
                terminalLimitsVO.setWeeklyAmountLimit(rs.getDouble("weekly_amount_limit"));
                terminalLimitsVO.setMonthlyAmountLimit(rs.getDouble("monthly_amount_limit"));
                terminalLimitsVO.setDailyCardAmountLimit(rs.getDouble("daily_card_amount_limit"));
                terminalLimitsVO.setWeeklyCardAmountLimit(rs.getDouble("weekly_card_amount_limit"));
                terminalLimitsVO.setMonthlyCardAmountLimit(rs.getDouble("monthly_card_amount_limit"));
                terminalLimitsVO.setDailyAvgTicketAmount(rs.getDouble("daily_avg_ticket"));
                terminalLimitsVO.setWeeklyAvgTicketAmount(rs.getDouble("weekly_avg_ticket"));
                terminalLimitsVO.setMonthlyAvgTicketAmount(rs.getDouble("monthly_avg_ticket"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminal limits", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalProcessingLimitVO()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminal limits", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getMemberTerminalProcessingLimitVO()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalLimitsVO;
    }

    public TerminalVO getMemberTerminalDetails(String terminalid, String memberId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query="SELECT gt.currency,mam.addressValidation,mam.addressDetails,mam.isTokenizationActive,gt.gateway,mam.isManualRecurring,mam.is_recurring,mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck,mam.emi_support, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc \n" +
                    "FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND terminalid =? AND memberid=?";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,terminalid);
            pstmt.setString(2,memberId);
            logger.debug("qury-->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setIsEmi_support(rs.getString("emi_support"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getMemberTerminalDetails(String terminalid, String memberId, String currency) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query = new StringBuffer();
        try
        {
            connection=Database.getRDBConnection();
            query.append("SELECT gt.currency,mam.addressValidation,mam.addressDetails,mam.isTokenizationActive,gt.gateway,mam.isManualRecurring,mam.is_recurring,mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount \n" +
                    "FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND terminalid =? AND memberid=? AND currency=?");
            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1,terminalid);
            pstmt.setString(2,memberId);
            pstmt.setString(3,currency);
            logger.debug("qury-->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getPartnerTerminalfromPartnerAndTerminal(String partnerId,String terminalId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query = "SELECT m.memberid,m.partnerid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,mam.addressValidation FROM member_account_mapping AS mam, members AS m WHERE m.partnerId=? AND mam.terminalid=? AND mam.memberid = m.memberid ";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,partnerId);
            pstmt.setString(2, terminalId);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public boolean isPartnersMerchantMappedWithTerminal(String partnerId,String terminalId) throws PZDBViolationException
    {
        boolean isTerminalExist = false;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query = "SELECT m.memberid,m.partnerid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive FROM member_account_mapping AS mam, members AS m WHERE m.partnerId=? AND mam.terminalid=? AND mam.memberid = m.memberid ";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,terminalId);
            pstmt.setString(2,partnerId);
            logger.debug("Query--->"+pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                isTerminalExist = true;
            }
        }
        catch (SystemError systemError)
        {
            isTerminalExist = false;
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            isTerminalExist = false;
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return isTerminalExist;
    }

    public TerminalVO getTerminalDetailsWithPartnerId(String partnerId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT m.partnerid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.isTokenizationActive,mam.isActive,mam.accountid FROM member_account_mapping AS mam,gateway_accounts AS ga,members AS m, gateway_type AS gt WHERE m.partnerid=? AND mam.memberid=m.memberid AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.isActive='Y'";
            ps = con.prepareStatement(qry);
            ps.setString(1,partnerId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            logger.debug("query for partner token without terminalid----" + ps);
            rs = ps.executeQuery();
            if(rs.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setPartnerId(rs.getString("partnerid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVO.setIsActive(rs.getString("isActive"));
                terminalVO.setCurrency(currency);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public HashMap getListofPaymentandCardtype(String toid)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();

        List<String> cardList = null;
        Connection conn = null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT paymodeid FROM member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE memberid=? AND currency='ALL' AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, toid);
            logger.debug("query for paymentType ---" + pstmt);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                cardList = new ArrayList<String>();
                String query1 = "SELECT DISTINCT cardtypeid FROM member_account_mapping WHERE paymodeid=? AND memberid=? ";
                PreparedStatement pstmt1 = conn.prepareStatement(query1);
                pstmt1.setString(1,res.getString("paymodeid"));
                pstmt1.setString(2, toid);
                logger.debug("query1 for cardType---" + pstmt1);
                ResultSet res1 = pstmt1.executeQuery();
                while (res1.next())
                {
                    cardList.add(res1.getString("cardtypeid"));
                }
                map.put(res.getString("paymodeid"), cardList);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return map;
    }

    public HashMap getListofPaymentandCardtype(String toid,String currency,String accountId)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();
        List<String> cardList = null;
        Connection conn = null;
        Functions functions=new Functions();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT paymodeid FROM member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE memberid=? AND currency=? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
            if(functions.isValueNull(accountId))
                query.append(" AND m.accountid=?");
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toid);
            pstmt.setString(2, currency);
            if(functions.isValueNull(accountId))
                pstmt.setString(3,accountId);
            logger.debug("query for paymentType ---" + pstmt);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                cardList = new ArrayList<String>();
                StringBuffer query1 = new StringBuffer("SELECT DISTINCT cardtypeid FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND currency=? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
                if(functions.isValueNull(accountId))
                    query1.append(" AND m.accountid=?");
                PreparedStatement pstmt1 = conn.prepareStatement(query1.toString());
                pstmt1.setString(1,res.getString("paymodeid"));
                pstmt1.setString(2, toid);
                pstmt1.setString(3, currency);
                if(functions.isValueNull(accountId))
                    pstmt1.setString(4,accountId);
                logger.debug("query1 for cardType---" + pstmt1);
                ResultSet res1 = pstmt1.executeQuery();
                while (res1.next())
                {
                    cardList.add(res1.getString("cardtypeid"));
                }
                if(cardList.size()>0)
                map.put(res.getString("paymodeid"), cardList);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return map;
    }

    public HashMap getListofPaymentandCardtype1(String toid,String currency,String currency2,String accountId)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();
        List<String> cardList = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        Functions functions=new Functions();


        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT paymodeid FROM member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE memberid=? AND currency in (?,?) AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
            if(functions.isValueNull(accountId))
                query.append(" AND m.accountid=?");
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toid);
            pstmt.setString(2, currency);
            pstmt.setString(3, currency2);
            if(functions.isValueNull(accountId))
                pstmt.setString(4,accountId);
            logger.debug("query for paymentType ---" + pstmt);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                cardList = new ArrayList<String>();
                //String q = "SELECT DISTINCT cardtypeid FROM member_account_mapping WHERE paymodeid=? and memberid = ?";
                StringBuffer query1 = new StringBuffer("SELECT DISTINCT cardtypeid FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND currency in (?,?) AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
                if(functions.isValueNull(accountId))
                    query1.append(" AND m.accountid=?");
                PreparedStatement pstmt1 = conn.prepareStatement(query1.toString());
                pstmt1.setString(1,res.getString("paymodeid"));
                pstmt1.setString(2, toid);
                pstmt1.setString(3, currency);
                pstmt1.setString(4, currency2);
                if(functions.isValueNull(accountId))
                    pstmt1.setString(5,accountId);
                logger.debug("query1 for cardType---" + pstmt1);
                ResultSet res1 = pstmt1.executeQuery();
                while (res1.next())
                {
                    cardList.add(res1.getString("cardtypeid"));
                }
                if(cardList.size()>0)
                map.put(res.getString("paymodeid"), cardList);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return map;
    }

    public HashMap getListofPaymentandCardtype(String toid,String currency,String paymentType,String accountId)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();

        List<String> cardList = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        ResultSet res1=null;
        Functions functions=new Functions();
        try
        {
            conn = Database.getRDBConnection();

            cardList = new ArrayList<String>();
            //String q = "SELECT DISTINCT cardtypeid FROM member_account_mapping WHERE paymodeid=? and memberid = ?";
            StringBuffer query1 = new StringBuffer("SELECT DISTINCT cardtypeid FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND currency=? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
            if(functions.isValueNull(accountId))
                query1.append(" AND m.accountid=?");
            pstmt1 = conn.prepareStatement(query1.toString());
            pstmt1.setString(1, paymentType);
            pstmt1.setString(2, toid);
            pstmt1.setString(3, currency);
            if(functions.isValueNull(accountId))
                pstmt1.setString(4,accountId);
            logger.debug("query1 for cardType---" + pstmt1);
            res1 = pstmt1.executeQuery();
            while (res1.next())
            {
                cardList.add(res1.getString("cardtypeid"));
            }
            if(cardList.size()>0)
            map.put(paymentType, cardList);

        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeResultSet(res1);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return map;
    }

    public HashMap getListofPaymentandCardtype(String toid,String currency1,String currency,String paymentType,String accountId)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();

        List<String> cardList = null;
        Connection conn = null;
        PreparedStatement pstmt1=null;
        ResultSet res1=null;
        Functions functions=new Functions();
        try
        {
            conn = Database.getRDBConnection();

            cardList = new ArrayList<String>();
            //String q = "SELECT DISTINCT cardtypeid FROM member_account_mapping WHERE paymodeid=? and memberid = ?";
            StringBuffer query1 = new StringBuffer("SELECT DISTINCT cardtypeid FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND currency IN (?,?) AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
            if(functions.isValueNull(accountId))
                query1.append(" AND m.accountid=?");
            pstmt1 = conn.prepareStatement(query1.toString());
            pstmt1.setString(1, paymentType);
            pstmt1.setString(2, toid);
            pstmt1.setString(3, currency1);
            pstmt1.setString(4, currency);
            if(functions.isValueNull(accountId))
                pstmt1.setString(5,accountId);
            logger.debug("query1 for cardType---" + pstmt1);
            res1 = pstmt1.executeQuery();
            while (res1.next())
            {
                cardList.add(res1.getString("cardtypeid"));
            }
            if(cardList.size()>0)
            map.put(paymentType, cardList);

        }
        catch(SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtype()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeResultSet(res1);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return map;
    }

    public HashMap getTerminaliIDForSKNewFlow(String toid,String terminalid)
    {
        HashMap map = new HashMap();
        Connection conn = null;
        PreparedStatement pstmt2=null;
        ResultSet res2=null;
        String paymodeid="";
        List<String> cardTypeList = new ArrayList<String>();
        try
        {
            conn = Database.getRDBConnection();
            String query2 = "SELECT paymodeid,cardtypeid FROM member_account_mapping WHERE terminalid=? AND memberid=? AND isActive='Y'";
            pstmt2 = conn.prepareStatement(query2);
            pstmt2.setString(1, terminalid);
            pstmt2.setString(2, toid);
            logger.debug("query2 for terminalid---" + pstmt2);
            res2 = pstmt2.executeQuery();
            while (res2.next())
            {
                paymodeid = res2.getString("paymodeid");
                cardTypeList.add(res2.getString("cardtypeid"));
            }
            if(cardTypeList.size()>0)
                map.put(paymodeid,cardTypeList);
        }
        catch (SQLException e)
        {
            logger.error("SQLException-->",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError-->",systemError);
        }
        finally
        {
            Database.closeResultSet(res2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }
        return map;
    }

    public TerminalVO getPartnersTerminalDetails(String partnerId, String payModeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer qry = new StringBuffer();

        try
        {
            con = Database.getRDBConnection();
            qry.append("SELECT mam.paymodeid,mam.cardtypeid,mam.accountid,mam.terminalid,mam.max_transaction_amount,mam.min_transaction_amount,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.memberid,gt.gateway,mam.isTokenizationActive,mam.addressDetails,mam.addressValidation,mam.isActive,p.isAddressRequiredForTokenTransaction\n" +
                    "FROM member_account_mapping AS mam, partners AS p, gateway_type AS gt, gateway_accounts AS ga\n" +
                    "WHERE p.partnerId=gt.partnerid AND p.partnerId= ga.partnerid AND gt.pgtypeid=ga.pgtypeid AND mam.accountid=ga.accountid AND p.partnerId=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=?");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1,partnerId);
            pstmt.setString(2,payModeId);
            pstmt.setString(3, cardTypeId);
            pstmt.setString(4, currency);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO = new TerminalVO();
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("memberid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("isAddressRequiredForTokenTransaction")); //Partner level address validation flag checked
                    return terminalVO;
                }
                else
                    terminalVO.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting terminal details based on Partners",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPartnersTerminalDetails()", null, "Common", "System error while getting terminal details based on partner", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting terminal details based on Partners",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPartnersTerminalDetails   ()", null, "Common", "Sql error while getting terminal details based on partner", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public boolean isGatewayCurrencyExistWithMember(String memberId, String currency) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        boolean isGatewayCurrencyExistWithMember = false;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT gt.currency FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt \n" +
                    "WHERE mam.memberid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2, currency);
            logger.debug("isGatewayCurrencyExistWithMember ps----" + ps);
            rs = ps.executeQuery();
            if(rs.next())
            {
                isGatewayCurrencyExistWithMember = true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while finding isGatewayCurrencyExistWithMember",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "isGatewayCurrencyExistWithMember()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while finding isGatewayCurrencyExistWithMember",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "isGatewayCurrencyExistWithMember()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return isGatewayCurrencyExistWithMember;
    }

    public List<TerminalVO> getCreateRegistrationForMerchant(String memberid) throws PZDBViolationException
    {
        List<TerminalVO> terminalVOs = new ArrayList();
        TerminalVO terminalVO = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT DISTINCT pt.paymentType FROM payment_type AS pt JOIN member_account_mapping AS mam WHERE pt.paymodeid=mam.paymodeid AND mam.memberid=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,memberid);
            logger.debug("query for getCreateRegistrationForMerchant()-----" + pstmt);
            transactionLogger.debug("query for getCreateRegistrationForMerchant()-----" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO=new TerminalVO();
                terminalVO.setPaymentTypeName(rs.getString("paymentType"));
                terminalVOs.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCreateRegistrationForMerchant()", null, "Common", "System error while Connecting to member_account_mapping and payment_type", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting paymodeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCreateRegistrationForMerchant()", null, "Common", "System error while Connecting to member_account_mapping and payment_type", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVOs;
    }

    public List<TerminalVO> getAllMappedTerminals()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList = new ArrayList();
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name,m.partnerId,gt.currency, gt.gateway,gt.pgtypeid,gt.name,mam.isActive,mam.paymodeid,mam.cardtypeid,mam.terminalid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid ORDER BY gateway ASC";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                terminalVO = new TerminalVO();
                String accountid = res.getString("accountid");
                terminalVO.setAccountId(accountid);
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setContactPerson(res.getString("contact_persons"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setGateway_id(res.getString("pgtypeid"));
                terminalVO.setGateway_name(res.getString("name"));
                terminalVO.setIsActive(res.getString("isActive"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setCompany_name(res.getString("company_name"));
                terminalVO.setPartnerId(res.getString("partnerId"));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public List<TerminalVO> getAllMappedTerminals1(String gatewayid, String accountid, String memberid)throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList = new ArrayList();
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet res=null;
        Functions functions = new Functions();

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name,m.partnerId,gt.currency, gt.gateway,gt.pgtypeid,gt.name,mam.isActive,mam.paymodeid,mam.cardtypeid,mam.terminalid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid ";

            if (functions.isEmptyOrNull(gatewayid) && functions.isEmptyOrNull(accountid) && functions.isEmptyOrNull(memberid))
            {
                query = query + " ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);

            }
            else if (functions.isValueNull(gatewayid)&& functions.isEmptyOrNull(accountid) && functions.isEmptyOrNull(memberid))
            {
                query = query + " AND ga.pgtypeid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,gatewayid);
            }
            else if (functions.isEmptyOrNull(gatewayid)&& functions.isValueNull(accountid)  && functions.isEmptyOrNull(memberid))
            {
                query = query + " AND mam.accountid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,accountid);
            }
            else if (functions.isEmptyOrNull(gatewayid)&& functions.isEmptyOrNull(accountid)  && functions.isValueNull(memberid))
            {
                query = query + " AND mam.memberid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,memberid);
            }
            else if (functions.isValueNull(gatewayid)&& functions.isValueNull(accountid) && functions.isEmptyOrNull(memberid))
            {
                query = query + " AND ga.pgtypeid = ? AND mam.accountid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,gatewayid);
                pstmt.setString(2, accountid);
            }
            else if (functions.isEmptyOrNull(gatewayid)&& functions.isValueNull(accountid) && functions.isValueNull(memberid))
            {
                query = query + " AND mam.accountid = ? AND mam.memberid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,accountid);
                pstmt.setString(2, memberid);
            }
            else if (functions.isValueNull(gatewayid)&& functions.isEmptyOrNull(accountid) && functions.isValueNull(memberid))
            {
                query = query + " AND ga.pgtypeid = ? AND mam.memberid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,gatewayid);
                pstmt.setString(2, memberid);
            }
            else if (functions.isValueNull(gatewayid)&& functions.isValueNull(accountid) && functions.isValueNull(memberid))
            {
                query = query + " AND ga.pgtypeid = ? AND mam.accountid = ? AND mam.memberid = ? ORDER BY mam.memberid ASC";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,gatewayid);
                pstmt.setString(2,accountid);
                pstmt.setString(3, memberid);
            }

            res = pstmt.executeQuery();
            while (res.next())
            {
                terminalVO = new TerminalVO();
                terminalVO.setAccountId(res.getString("accountid"));
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setGateway_id(res.getString("pgtypeid"));
                terminalVO.setCompany_name(res.getString("company_name"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public boolean isGatewayMemberMapped(String pgtypeid, String memberid)
    {
        logger.debug("inside isGatewayMemberMapped");
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String mappedmember = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name,m.partnerId,gt.currency, gt.gateway,gt.pgtypeid,gt.name,mam.isActive,mam.paymodeid,mam.cardtypeid,mam.terminalid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid AND gt.pgtypeid = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pgtypeid);
            logger.debug("isGatewayMemberMapped query::::::"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                mappedmember = rs.getString("memberid");
            }
        }
        catch (Exception e)
        {
            logger.debug("Exception e");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        if (memberid.equals(mappedmember))
            return true;
        return false;
    }

    public List<TerminalVO> getAllMappedTerminal()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList = new ArrayList();
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name,m.partnerId,gt.currency, gt.gateway,gt.pgtypeid,gt.name,mam.isActive,mam.paymodeid,mam.cardtypeid,mam.terminalid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid ORDER BY mam.terminalid ASC";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                terminalVO = new TerminalVO();
                String accountid = res.getString("accountid");
                terminalVO.setAccountId(accountid);
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setContactPerson(res.getString("contact_persons"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setGateway_id(res.getString("pgtypeid"));
                terminalVO.setGateway_name(res.getString("name"));
                terminalVO.setIsActive(res.getString("isActive"));
                terminalVO.setTerminalId(res.getString("terminalid"));
                terminalVO.setCardType(GatewayAccountService.getCardType(res.getString("cardtypeid")));
                terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(res.getString("paymodeid")));
                terminalVO.setPaymodeId(res.getString("paymodeid"));
                terminalVO.setCardTypeId(res.getString("cardtypeid"));
                terminalVO.setCompany_name(res.getString("company_name"));
                terminalVO.setPartnerId(res.getString("partnerId"));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public List<TerminalVO> getAllMappedTerminalsForCommon()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList = new ArrayList();
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name, gt.currency, gt.gateway,gt.pgtypeid,gt.name FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE gt.gateway AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid ORDER BY gateway ASC";

            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                terminalVO = new TerminalVO();
                String accountid = res.getString("accountid");
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(accountid);
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setContactPerson(res.getString("contact_persons"));
                terminalVO.setGateway_name(res.getString("name"));
                terminalVO.setGateway_id(res.getString("pgtypeid"));
                terminalVO.setCompany_name(res.getString("company_name"));

                terminalVOList.add(terminalVO);
            }

            logger.debug("query----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public List<TerminalVO> getAllMappedAccounts()throws PZDBViolationException
    {
        List<TerminalVO> terminalVOList = new ArrayList();
        TerminalVO terminalVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT DISTINCT mam.accountid,ga.merchantid,gt.currency,gt.gateway,gt.pgtypeid,gt.name FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ORDER BY gateway ASC";
            pstmt = conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while (res.next())
            {
                terminalVO = new TerminalVO();
                String accountid = res.getString("accountid");
                terminalVO.setAccountId(accountid);
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setGateway_id(res.getString("pgtypeid"));
                terminalVOList.add(terminalVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllMappedAccounts()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ", e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllMappedAccounts()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return terminalVOList;
    }

    public Hashtable getListOfTerminalDetails(String memberId, String accountId)throws  PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            Functions functions=new Functions();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("select memberid,accountid,paymodeid,cardtypeid,monthly_amount_limit,daily_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isTest,terminalid,weekly_amount_limit,is_recurring,isRestrictedTicketActive,isTokenizationActive,isManualRecurring,addressDetails,addressValidation,cardDetailRequired,isPSTTerminal,isCardEncryptionEnable,riskruleactivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket from member_account_mapping as M where accountid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from member_account_mapping where accountid>0 ");
            if (functions.isValueNull(memberId))
            {
                query.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberId));
                countquery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberId));
                // url+="&memberid=\""+memberid+"\"";
            }
            if(functions.isValueNull(accountId) && !"0".equalsIgnoreCase(accountId))
            {
                query.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
                countquery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountId));
                //url+="&accountid=\""+accountid+"\"";
            }
            query.append(" order by memberid ");
            logger.debug("---query---"+query);



            query.append(" order by M.memberid ");
            logger.debug("---query---"+query);
            if ( true)
            {
                hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
                rs = Database.executeQuery(countquery.toString(), conn);
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                    hash.put("records", "" + (hash.size() - 2));
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException("TerminalDAO","getListOfTerminalDetails()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",se);
            PZExceptionHandler.raiseDBViolationException("TerminalDAO","getListOfTerminalDetails()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return  hash;
    }

    public  LinkedHashMap<String,String> loadPaymodeids()
    {
        LinkedHashMap<String,String> paymodeids=new LinkedHashMap<String,String>();
        Connection conn=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select * from payment_type order by paymentType", conn);
            while (rs.next())
            {
                int paymodeid = rs.getInt("paymodeid");
                String paymode = rs.getString("paymentType");
                paymodeids.put(paymodeid + "", paymode);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return paymodeids;
    }

    public LinkedHashMap<String,String> loadcardtypeids()
    {
        LinkedHashMap<String,String> cardtypeids=new LinkedHashMap<String,String>();
        Connection conn=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select * from card_type order by cardType", conn);
            while (rs.next())
            {
                cardtypeids.put(rs.getInt("cardtypeid") + "", rs.getString("cardType"));
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return cardtypeids;
    }

    public  boolean isMasterCardSupported(String accountId)throws Exception
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean flag=false;

        try
        {
            conn=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select ismastercardsupported from gateway_accounts where accountid=?");
            pstmt=conn.prepareStatement(sb.toString());
            pstmt.setString(1, accountId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                if("1".equals(rs.getString("ismastercardsupported")))
                {
                    flag=true;
                }
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return flag;
    }

    public boolean isTerminalUnique(String memberId,String accountId,String payModeId,String cardTypeId)throws Exception
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        boolean status          = true;
        try
        {
            conn            = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("select terminalid from member_account_mapping where memberid=? and accountid=? and paymodeid=? and cardtypeid=?");
            pstmt           = conn.prepareStatement(sb.toString());

            pstmt.setString(1,memberId);
            pstmt.setString(2,accountId);
            pstmt.setString(3,payModeId);
            pstmt.setString(4,cardTypeId);

            rs  = pstmt.executeQuery();

            if(rs.next())
            {
                status  = false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public boolean isValidTerminal(String memberId, String accountId,String terminalId,String payModeId, String cardTypeId)throws Exception
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=true;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select terminalid from member_account_mapping where memberid=? and accountid=? and terminalid=? and paymodeid=? and cardtypeid=?");
            pstmt=conn.prepareStatement(sb.toString());
            pstmt.setString(1,memberId);
            pstmt.setString(2,accountId);
            pstmt.setString(3,terminalId);
            pstmt.setString(4,payModeId);
            pstmt.setString(5,cardTypeId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }

    public String getCardType(String cardtypeid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String cardType=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select cardType from card_type where cardtypeid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, cardtypeid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                cardType = rs.getString("cardType");
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
        return cardType;
    }

    public String getPaymentType(String paymodeid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String paymentType=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select paymentType from payment_type where paymodeid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, paymodeid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                paymentType = rs.getString("paymentType");
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
        return paymentType;
    }

    public  String updateTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String success="";
        try
        {
            conn=Database.getConnection();
            String query="update member_account_mapping set daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,daily_card_amount_limit=?," +
                          "weekly_card_amount_limit=?,monthly_card_amount_limit=?,min_transaction_amount=?,max_transaction_amount=?,isActive=?,priority=?,isTest=?,weekly_amount_limit=?," +
                          "is_recurring=?,isRestrictedTicketActive=?,isTokenizationActive=?,isManualRecurring=?,addressDetails=?,addressValidation=?,cardDetailRequired=?,isPSTTerminal=?," +
                          "isCardEncryptionEnable=?,riskruleactivation=?,daily_avg_ticket=?,weekly_avg_ticket=?,monthly_avg_ticket=?,settlement_currency=?,min_payout_amount=?,payoutActivation=?," +
                          "autoRedirectRequest=?,isCardWhitelisted=?,isEmailWhitelisted=?,currency_conversion=?,conversion_currency=?,binRouting=?,emi_support=?,whitelisting_details=?," +
                          "cardLimitCheck=?,cardAmountLimitCheck=?,amountLimitCheck=?,processor_partnerid=?,payout_priority=?,daily_amount_limit_check=?,weekly_amount_limit_check=?," +
                          "monthly_amount_limit_check=?,daily_card_limit_check=?,weekly_card_limit_check=?,monthly_card_limit_check=?,daily_card_amount_limit_check=?,weekly_card_amount_limit_check=?," +
                          "monthly_card_amount_limit_check=?  where memberid=? and accountid=? and terminalid=?";
            PreparedStatement pstmnt=conn.prepareStatement(query);
            pstmnt.setString(1,terminalVO.getDaily_amount_limit());
            pstmnt.setString(2,terminalVO.getMonthly_amount_limit());
            pstmnt.setString(3,terminalVO.getDaily_card_limit());
            pstmnt.setString(4,terminalVO.getWeekly_card_limit());
            pstmnt.setString(5,terminalVO.getMonthly_card_limit());
            pstmnt.setString(6,terminalVO.getDaily_card_amount_limit());
            pstmnt.setString(7,terminalVO.getWeekly_card_amount_limit());
            pstmnt.setString(8,terminalVO.getMonthly_card_amount_limit());
            pstmnt.setString(9,terminalVO.getMin_trans_amount());
            pstmnt.setString(10,terminalVO.getMax_trans_amount());
            pstmnt.setString(11,terminalVO.getIsActive());
            pstmnt.setString(12,terminalVO.getPriority());
            pstmnt.setString(13,terminalVO.getIsTest());
            pstmnt.setString(14,terminalVO.getWeekly_amount_limit());
            pstmnt.setString(15,terminalVO.getIsRecurring());
            pstmnt.setString(16,terminalVO.getIsRestrictedTicketActive());
            pstmnt.setString(17,terminalVO.getIsTokenizationActive());
            pstmnt.setString(18,terminalVO.getIsManualRecurring());
            pstmnt.setString(19,terminalVO.getAddressDetails());
            pstmnt.setString(20,terminalVO.getAddressValidation());
            pstmnt.setString(21,terminalVO.getCardDetailRequired());
            pstmnt.setString(22,terminalVO.getIsPSTTerminal());
            pstmnt.setString(23,terminalVO.getIsCardEncryptionEnable());
            pstmnt.setString(24,terminalVO.getRiskRuleActivation());
            pstmnt.setString(25,terminalVO.getDaily_avg_ticket());
            pstmnt.setString(26,terminalVO.getWeekly_avg_ticket());
            pstmnt.setString(27,terminalVO.getMonthly_avg_ticket());
            pstmnt.setString(28,terminalVO.getSettlementCurrency());
            pstmnt.setString(29,terminalVO.getMinPayoutAmount());
            pstmnt.setString(30,terminalVO.getPayoutActivation());
            pstmnt.setString(31,terminalVO.getAutoRedirectRequest());
            pstmnt.setString(32,terminalVO.getIsCardWhitelisted());
            pstmnt.setString(33,terminalVO.getIsEmailWhitelisted());
            pstmnt.setString(34,terminalVO.getCurrencyConversion());
            pstmnt.setString(35,terminalVO.getConversionCurrency());
            pstmnt.setString(36,terminalVO.getIsbin_routing());
            pstmnt.setString(37,terminalVO.getIsEmi_support());
            pstmnt.setString(38,terminalVO.getWhitelisting());
            pstmnt.setString(39,terminalVO.getCardLimitCheckTerminalLevel());
            pstmnt.setString(40,terminalVO.getCardAmountLimitCheckTerminalLevel());
            pstmnt.setString(41,terminalVO.getAmountLimitCheckTerminalLevel());
            pstmnt.setString(42,terminalVO.getProcessor_partnerid());
            pstmnt.setString(43,terminalVO.getPayout_priority());
            pstmnt.setString(44,terminalVO.getDaily_amount_limit_check());
            pstmnt.setString(45,terminalVO.getWeekly_amount_limit_check());
            pstmnt.setString(46,terminalVO.getMonthly_amount_limit_check());
            pstmnt.setString(47,terminalVO.getDaily_card_limit_check());
            pstmnt.setString(48,terminalVO.getWeekly_card_limit_check());
            pstmnt.setString(49,terminalVO.getMonthly_card_limit_check());
            pstmnt.setString(50,terminalVO.getDaily_card_amount_limit_check());
            pstmnt.setString(51,terminalVO.getWeekly_card_amount_limit_check());
            pstmnt.setString(52,terminalVO.getMonthly_card_amount_limit_check());
            pstmnt.setString(53,terminalVO.getMemberId());
            pstmnt.setString(54,terminalVO.getAccountId());
            pstmnt.setString(55, terminalVO.getTerminalId());
            logger.debug("Update Query for terminal--------" + pstmnt);

            int k=pstmnt.executeUpdate();
            if(k>0)
            {
                success="Terminal Configuration Updated Successfully.";
            }
            else
            {
                success="Could Not Update Terminal Configuration For " + terminalVO.getTerminalId();
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",e);
        }
        catch (SystemError se)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  success;
    }

    public  String masterCardTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String success="";
        try
        {
            conn=Database.getConnection();
            String query = "insert into  member_account_mapping(daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,memberid,accountid,paymodeid,cardtypeid,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isTest,weekly_amount_limit,is_recurring,isRestrictedTicketActive,isTokenizationActive,addressDetails,addressValidation,cardDetailRequired,isPSTTerminal,isManualRecurring,isCardEncryptionEnable,riskruleactivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket,settlement_currency,min_payout_amount,payoutActivation,autoRedirectRequest,isCardWhitelisted,isEmailWhitelisted,currency_conversion,conversion_currency,binRouting,emi_support,whitelisting_details,cardLimitCheck,cardAmountLimitCheck,amountLimitCheck,actionExecutorId,actionExecutorName,processor_partnerid,payout_priority,daily_amount_limit_check,weekly_amount_limit_check,monthly_amount_limit_check,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,daily_card_amount_limit_check,weekly_card_amount_limit_check,monthly_card_amount_limit_check,dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, terminalVO.getDaily_amount_limit());
            pstmnt.setString(2, terminalVO.getMonthly_amount_limit());
            pstmnt.setString(3, terminalVO.getDaily_card_limit());
            pstmnt.setString(4, terminalVO.getWeekly_card_limit());
            pstmnt.setString(5, terminalVO.getMonthly_card_limit());
            pstmnt.setString(6, terminalVO.getMemberId());
            pstmnt.setString(7, terminalVO.getAccountId());
            pstmnt.setString(8, terminalVO.getPaymodeId());
            pstmnt.setString(9, terminalVO.getCardTypeId());
            pstmnt.setString(10, terminalVO.getDaily_card_amount_limit());
            pstmnt.setString(11, terminalVO.getWeekly_card_amount_limit());
            pstmnt.setString(12, terminalVO.getMonthly_card_amount_limit());
            pstmnt.setString(13, terminalVO.getMin_trans_amount());
            pstmnt.setString(14, terminalVO.getMax_trans_amount());
            pstmnt.setString(15, terminalVO.getIsActive());
            pstmnt.setString(16, terminalVO.getPriority());
            pstmnt.setString(17, terminalVO.getIsTest());
            pstmnt.setString(18, terminalVO.getWeekly_amount_limit());
            pstmnt.setString(19, terminalVO.getIsRecurring());
            pstmnt.setString(20, terminalVO.getIsRestrictedTicketActive());
            pstmnt.setString(21, terminalVO.getIsTokenizationActive());
            pstmnt.setString(22, terminalVO.getAddressDetails());
            pstmnt.setString(23, terminalVO.getAddressValidation());
            pstmnt.setString(24, terminalVO.getCardDetailRequired());
            pstmnt.setString(25, terminalVO.getIsPSTTerminal());
            pstmnt.setString(26, terminalVO.getIsManualRecurring());
            pstmnt.setString(27, terminalVO.getIsCardEncryptionEnable());
            pstmnt.setString(28, terminalVO.getRiskRuleActivation());
            pstmnt.setString(29, terminalVO.getDaily_avg_ticket());
            pstmnt.setString(30, terminalVO.getWeekly_avg_ticket());
            pstmnt.setString(31, terminalVO.getMonthly_avg_ticket());
            pstmnt.setString(32, terminalVO.getSettlementCurrency());
            pstmnt.setString(33, terminalVO.getMinPayoutAmount());
            pstmnt.setString(34, terminalVO.getPayoutActivation());
            pstmnt.setString(35, terminalVO.getAutoRedirectRequest());
            pstmnt.setString(36,terminalVO.getIsCardWhitelisted());
            pstmnt.setString(37,terminalVO.getIsEmailWhitelisted());
            pstmnt.setString(38,terminalVO.getCurrencyConversion());
            pstmnt.setString(39,terminalVO.getConversionCurrency());
            pstmnt.setString(40,terminalVO.getIsbin_routing());
            pstmnt.setString(41,terminalVO.getIsEmi_support());
            pstmnt.setString(42,terminalVO.getWhitelisting());
            pstmnt.setString(43,terminalVO.getCardLimitCheckTerminalLevel());
            pstmnt.setString(44, terminalVO.getCardAmountLimitCheckTerminalLevel());
            pstmnt.setString(45,terminalVO.getAmountLimitCheckTerminalLevel());
            pstmnt.setString(46,terminalVO.getActionExecutorId());
            pstmnt.setString(47,terminalVO.getActionExecutorName());
            pstmnt.setString(48,terminalVO.getProcessor_partnerid());
            pstmnt.setString(49,terminalVO.getPayout_priority());
            pstmnt.setString(50,terminalVO.getDaily_amount_limit_check());
            pstmnt.setString(51,terminalVO.getWeekly_amount_limit_check());
            pstmnt.setString(52,terminalVO.getMonthly_amount_limit_check());
            pstmnt.setString(53,terminalVO.getDaily_card_limit_check());
            pstmnt.setString(54,terminalVO.getWeekly_card_limit_check());
            pstmnt.setString(55,terminalVO.getMonthly_card_limit_check());
            pstmnt.setString(56,terminalVO.getDaily_card_amount_limit_check());
            pstmnt.setString(57,terminalVO.getWeekly_card_amount_limit_check());
            pstmnt.setString(58,terminalVO.getMonthly_card_amount_limit_check());

            logger.debug("create query === " + pstmnt);
            int k = pstmnt.executeUpdate();
            if (k > 0)
            {
                success = "New Terminal Added Successfully.";
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",e);
        }
        catch (SystemError se)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  success;
    }

    public  String deleteTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        Connection conn=null;
        ResultSet rs=null;
        PreparedStatement pstmnt=null;
        Hashtable hash=new Hashtable();
        String success="";
        try
        {
            conn=Database.getRDBConnection();
            String query="select * from member_account_mapping where memberid=? and accountid=? and terminalid=?";
            pstmnt=conn.prepareStatement(query);
            pstmnt.setString(1,terminalVO.getMemberId());
            pstmnt.setString(2,terminalVO.getAccountId());
            pstmnt.setString(3,terminalVO.getTerminalId());
            rs=pstmnt.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            int i = 0;
            transactionLogger.debug("query for terminal----"+pstmnt);
            while (rs.next())
            {
                for (i = 1; i <= count; i++)
                {
                    if (rs.getString(i) != null)
                    {
                        hash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }
            query="insert into  member_account_mapping_archive(memberid,accountid,paymodeid,cardtypeid,chargePercentage,fixApprovalCharge,fixDeclinedCharge,taxper,reversalcharge,withdrawalcharge,chargebackcharge,reservePercentage,fraudVerificationCharge,annualCharge,setupCharge,fxClearanceChargePercentage,monthlyGatewayCharge,monthlyAccountMntCharge,reportCharge,fraudulentCharge,autoRepresentationCharge,interchangePlusCharge,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isTest,addressDetails,addressValidation,cardDetailRequired,isPSTTerminal,isCardEncryptionEnable,riskruleactivation,payoutActivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket,terminalid,reject3DCard,isCardWhitelisted,isEmailWhitelisted,processor_partnerid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmnt=conn.prepareStatement(query);
            pstmnt.setString(1,(String)hash.get("memberid"));
            pstmnt.setString(2,(String)hash.get("accountid"));
            pstmnt.setString(3,(String)hash.get("paymodeid"));
            pstmnt.setString(4,(String)hash.get("cardtypeid"));
            pstmnt.setString(5,(String)hash.get("chargePercentage"));
            pstmnt.setString(6,(String)hash.get("fixApprovalCharge"));
            pstmnt.setString(7,(String)hash.get("fixDeclinedCharge"));
            pstmnt.setString(8,(String)hash.get("taxper"));
            pstmnt.setString(9,(String)hash.get("reversalcharge"));
            pstmnt.setString(10,(String)hash.get("withdrawalcharge"));
            pstmnt.setString(11,(String)hash.get("chargebackcharge"));
            pstmnt.setString(12,(String)hash.get("reservePercentage"));
            pstmnt.setString(13,(String)hash.get("fraudVerificationCharge"));
            pstmnt.setString(14,(String)hash.get("annualCharge"));
            pstmnt.setString(15,(String)hash.get("setupCharge"));
            pstmnt.setString(16,(String)hash.get("fxClearanceChargePercentage"));
            pstmnt.setString(17,(String)hash.get("monthlyGatewayCharge"));
            pstmnt.setString(18,(String)hash.get("monthlyAccountMntCharge"));
            pstmnt.setString(19,(String)hash.get("reportCharge"));
            pstmnt.setString(20,(String)hash.get("fraudulentCharge"));
            pstmnt.setString(21,(String)hash.get("autoRepresentationCharge"));
            pstmnt.setString(22,(String)hash.get("interchangePlusCharge"));
            pstmnt.setString(23,(String)hash.get("daily_amount_limit"));
            pstmnt.setString(24,(String)hash.get("monthly_amount_limit"));
            pstmnt.setString(25,(String)hash.get("daily_card_limit"));
            pstmnt.setString(26,(String)hash.get("weekly_card_limit"));
            pstmnt.setString(27,(String)hash.get("monthly_card_limit"));
            pstmnt.setString(28,(String)hash.get("daily_card_amount_limit"));
            pstmnt.setString(29,(String)hash.get("weekly_card_amount_limit"));
            pstmnt.setString(30,(String)hash.get("monthly_card_amount_limit"));
            pstmnt.setString(31,(String)hash.get("min_trans_amount"));
            pstmnt.setString(32,(String)hash.get("max_trans_amount"));
            pstmnt.setString(33,(String)hash.get("isActive"));
            pstmnt.setString(34,(String)hash.get("priority"));
            pstmnt.setString(35,(String)hash.get("isTest"));
            pstmnt.setString(36,(String)hash.get("addressDetails"));
            pstmnt.setString(37,(String)hash.get("addressValidation"));
            pstmnt.setString(38,(String)hash.get("cardDetailRequired"));
            pstmnt.setString(39,(String)hash.get("isPSTTerminal"));
            pstmnt.setString(40,(String)hash.get("isCardEncryptionEnable"));
            pstmnt.setString(41,(String)hash.get("riskruleactivation"));
            pstmnt.setString(42,(String)hash.get("payoutActivation"));
            pstmnt.setString(43,(String)hash.get("daily_avg_ticket"));
            pstmnt.setString(44,(String)hash.get("weekly_avg_ticket"));
            pstmnt.setString(45,(String)hash.get("monthly_avg_ticket"));
            pstmnt.setString(46,(String) hash.get("terminalid"));
            pstmnt.setString(47,(String) hash.get("reject3DCard"));
            pstmnt.setString(48,(String) hash.get("isCardWhitelisted"));
            pstmnt.setString(49,(String) hash.get("isEmailWhitelisted"));
            pstmnt.setString(50,(String) hash.get("processor_partnerid"));
            int k=pstmnt.executeUpdate();
            if(k>0)
            {
                query="delete from member_account_mapping  where memberid=? and accountid=? and terminalid=?";
                pstmnt=conn.prepareStatement(query);
                pstmnt.setString(1,terminalVO.getMemberId());
                pstmnt.setString(2,terminalVO.getAccountId());
                pstmnt.setString(3,terminalVO.getTerminalId());
                int l=pstmnt.executeUpdate();
                transactionLogger.debug("delete query----"+pstmnt);
                if(l>0)
                {
                    success = "Terminal Configuration Has Removed Successfully for " + terminalVO.getTerminalId();
                }
                else
                {
                    success = "Terminal Configuration Could Not Removed for " + terminalVO.getTerminalId();
                }
            }

        }
        catch (SQLException e)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",e);
        }
        catch (SystemError se)
        {
            logger.error("Leaving TerminalDAO throwing SQL Exception as System Error :::: ",se);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmnt);
            Database.closeConnection(conn);
        }
        return  success;
    }

    public LinkedHashMap<String,Integer> loadPaymodeid()
    {
        LinkedHashMap<String,Integer> paymodeids=new LinkedHashMap<String, Integer>();
        Connection conn=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select * from payment_type order by paymentType", conn);
            while (rs.next())
            {
                paymodeids.put(rs.getString("paymentType"), rs.getInt("paymodeid"));
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return paymodeids;

    }

    public LinkedHashMap<String,Integer> loadcardtypeid()
    {
        LinkedHashMap<String,Integer> cardtypeids=new LinkedHashMap< String,Integer>();
        Connection conn=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select * from card_type order by cardType", conn);
            while (rs.next())
            {
                cardtypeids.put(rs.getString("cardType"), rs.getInt("cardtypeid"));
            }
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return cardtypeids;

    }

    public TreeMap<Integer,Integer> getTerminals()
    {
        TreeMap<Integer,Integer> terminalMap = new TreeMap<Integer, Integer>();
        Connection connection = null;
        ResultSet rs=null;
        try
        {
            {
                connection = Database.getRDBConnection();
                String query = "select terminalid,accountid from member_account_mapping";
                rs = Database.executeQuery(query,connection);
                while (rs.next())
                    terminalMap.put(rs.getInt("terminalid"),rs.getInt("accountid"));
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException---",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return terminalMap;
    }

    public  TerminalVO getTerminalDetails(String terminalId)
    {
        TerminalVO terminalVo = null;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        try
        {

            connection=Database.getRDBConnection();
            String selectStatement="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,isActive FROM member_account_mapping WHERE terminalid =?  ORDER BY terminalid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, terminalId);
            rs=preparedStatement.executeQuery();
            logger.debug("query----"+preparedStatement);

            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }
    public LinkedHashMap<String,TerminalVO> getTerminalMap()
    {
        TerminalVO terminalVO=null;
        Connection connection = null;
        ResultSet rs=null;
        LinkedHashMap<String,TerminalVO> terminalMap = new LinkedHashMap();

        try
        {
            connection = Database.getRDBConnection();
            String query = "select terminalid,memberid,paymodeid,cardtypeid,isActive from member_account_mapping order by terminalid asc";
            rs = Database.executeQuery(query,connection);
            while (rs.next()){
                terminalVO=new TerminalVO();
                String terminalId=rs.getString("terminalid");
                terminalVO.setTerminalId(terminalId);
                terminalVO.setMemberId(rs.getString("memberid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setIsActive(rs.getString("isActive"));
                terminalMap.put(terminalId,terminalVO);
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError while fetching terminal details::::::",e);
        }
        catch (SQLException e)
        {
            logger.error("SQLException while fetching terminal details:::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(connection);
        }
        return terminalMap;
    }

    public List<TerminalVO> getTerminalsByMemberAccountIdForPayoutReport(String memberId,String accountId) //sandip
    {
        List<TerminalVO> terminalVOList=new ArrayList();
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            String query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,settlement_currency,min_payout_amount FROM member_account_mapping WHERE memberid=? and accountid=? and payoutActivation='Y' ORDER BY terminalid";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,memberId);
            pstmt.setString(2,accountId);
            rs=pstmt.executeQuery();

            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setSettlementCurrency(rs.getString("settlement_currency"));
                terminalVo.setMinPayoutAmount(rs.getString("min_payout_amount"));
                terminalVOList.add(terminalVo);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVOList;
    }

    public TerminalVO getTerminalsByMemberAccountIdForPayoutReport(String memberId,String accountId,String terminalId)
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
//            String query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,settlement_currency,min_payout_amount FROM member_account_mapping WHERE memberid=? and accountid=? and terminalId=?  ORDER BY terminalid";
            String query="SELECT memberid,accountid,paymodeid,cardtypeid,terminalid,settlement_currency,min_payout_amount FROM member_account_mapping WHERE memberid=? and accountid IN("+accountId+") and terminalId=?  ORDER BY terminalid";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,memberId);
//            pstmt.setString(2,accountId);
            pstmt.setString(2,terminalId);
            rs=pstmt.executeQuery();
            logger.error("query-----------------"+pstmt);
            if(rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("paymodeid")));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setSettlementCurrency(rs.getString("settlement_currency"));
                terminalVo.setMinPayoutAmount(rs.getString("min_payout_amount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }
    public TerminalVO getMemberTerminalDetailsForTerminalChange(String memberId,String payModeId,String cardTypeId,String currency) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query=null;
        try
        {
            connection=Database.getRDBConnection();
            query="SELECT mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.isActive='Y'";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,memberId);
            pstmt.setString(2,payModeId);
            pstmt.setString(3,cardTypeId);
            pstmt.setString(4,currency);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getMemberTerminalDetailsForTerminalChange()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getMemberTerminalDetailsForTerminalChange()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getRoutedTerminalDetails(String memberId,String paymodeid,String cardtypeid,String currency)
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,gt.pgtypeid,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='N' ORDER BY mam.priority";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeid);
            ps.setString(3,cardtypeid);
            ps.setString(4,currency);
            logger.debug("getActivatedRoutedTerminalDetails ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public TerminalVO getBinRoutingTerminalDetails(String firstSix,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        TerminalVO terminalVo = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try
        {
            connection = Database.getRDBConnection();
            //query="SELECT terminalid,addressValidation,addressDetails,isEmailWhitelisted,isCardWhitelisted,is_recurring,isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid FROM member_account_mapping AS m, whitelist_bins AS b WHERE bin=? AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid";
            query="SELECT ga.merchantid,gt.pgtypeid,gt.gateway,m.isActive,terminalid,m.addressValidation,isTokenizationActive,addressDetails,isEmailWhitelisted,isCardWhitelisted,m.is_recurring,m.isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid,m.whitelisting_details,m.cardLimitCheck, m.cardAmountLimitCheck, m.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS m, whitelist_bins AS b,gateway_accounts AS ga, gateway_type AS gt WHERE ? BETWEEN startBin AND endBin AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid AND m.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND m.binRouting='Y'";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,firstSix);
            pstmt.setString(2,memberId);
            pstmt.setString(3,paymodeid);
            pstmt.setString(4,cardtypeid);
            pstmt.setString(5,currency);

            logger.debug("getBinRoutingTerminalDetails---"+pstmt);

            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setMemberId(rs.getString("merchantid"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setCurrency(currency);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(String memberId,String paymodeId, String cardTypeId, String currency,String firstSix) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc, mam.emi_support FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bins AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND ? BETWEEN startBin AND endBin AND mam.accountid=ga.accountid AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='Y'";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            ps.setString(5, firstSix);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public TerminalVO getAccountIdTerminalVOforBinRouting(String toid, int paymenttype, int cardtype,String firstSix)
    {
        String accountId="0";
        Connection con = null;
        TerminalVO terminalVO=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {
            con=Database.getRDBConnection();
            String query2 = "SELECT gt.currency,mam.accountid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.terminalid,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc, mam.emi_support FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bins AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.isActive='Y' AND ? BETWEEN startBin AND endBin AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND mam.binRouting='Y'";
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            pstmt2.setString(4, firstSix);
            transactionLogger.debug("getAccountIdTerminalVO pstmt2----" + pstmt2);

            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminalVO = new TerminalVO();
                accountId = rs2.getString("accountid");
                terminalVO.setMax_transaction_amount(rs2.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs2.getFloat("min_transaction_amount"));
                terminalVO.setTerminalId(rs2.getString("terminalid"));
                terminalVO.setIsActive(rs2.getString("isActive"));
                terminalVO.setAddressDetails(rs2.getString("addressDetails"));
                terminalVO.setAddressValidation(rs2.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs2.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs2.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs2.getString("isManualRecurring"));
                terminalVO.setIsPSTTerminal(rs2.getString("isPSTTerminal"));
                terminalVO.setAccountId(accountId);
                terminalVO.setCurrency(rs2.getString("currency"));
                terminalVO.setAutoRedirectRequest(rs2.getString("autoRedirectRequest"));
                terminalVO.setReject3DCard(rs2.getString("reject3DCard"));
                terminalVO.setCurrencyConversion(rs2.getString("currency_conversion"));
                terminalVO.setConversionCurrency(rs2.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs2.getString("isCardWhitelisted"));
                terminalVO.setIsEmailWhitelisted(rs2.getString("isEmailWhitelisted"));
                terminalVO.setWhitelisting(rs2.getString("whitelisting_details"));
                terminalVO.setCardLimitCheckTerminalLevel(rs2.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheckTerminalLevel(rs2.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs2.getString("amountLimitCheck"));
                terminalVO.setCardLimitCheckAccountLevel(rs2.getString("cardLimitAccountLevel"));
                terminalVO.setCardAmountLimitCheckAccountLevel(rs2.getString("cardAmountLimitCheckAcc"));
                terminalVO.setAmountLimitCheckAccountLevel(rs2.getString("amountLimitCheckAcc"));
                terminalVO.setIsEmi_support(rs2.getString("emi_support"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(con);
        }

        return terminalVO;
    }

    public LinkedHashMap<String,TerminalVO> getTerminalFromCurrency(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Functions functions=new Functions();
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        LinkedHashMap<String,TerminalVO> terminalVOMap=new LinkedHashMap<String,TerminalVO>();
        String memberId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String paymodeId = commonValidatorVO.getPaymentType();
        String cardTypeId = commonValidatorVO.getCardType();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid ,mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.paymodeid=?  AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='N'");

            if(functions.isValueNull(currency))
              qry.append(" AND gt.currency='"+currency+"'");
            transactionLogger.error("after curr before if 17 condition cardTypeId--> "+cardTypeId);

            if(!"19".equalsIgnoreCase(paymodeId)){
                transactionLogger.error("before if 17 condition cardTypeId--> "+cardTypeId);
                if("17".equalsIgnoreCase(paymodeId)&&("1201".equalsIgnoreCase(cardTypeId)||("161").equalsIgnoreCase(cardTypeId))){
                    transactionLogger.error("inside if 17 condition cardTypeId--> "+cardTypeId);
                    qry.append(" AND mam.cardtypeid='"+cardTypeId+"'");
                }
                else{
                    transactionLogger.error("else 17 condition cardTypeId--> "+cardTypeId);
                    if(!"19".equalsIgnoreCase(paymodeId)){
                        if(!"17".equalsIgnoreCase(paymodeId)){
                            qry.append(" AND mam.cardtypeid='"+cardTypeId+"'");
                        }
                    }

                }
            }


            qry.append(" ORDER BY mam.priority");

            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            //ps.setString(3,cardTypeId);
            logger.error("getTerminalFromCurrency ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setCurrency(currency);
                    terminalVOMap.put(rs.getString("terminalid"),terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVOMap;
    }
    public TerminalVO getBinRoutingTerminalDetailsByCardNumber(String cNum,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        TerminalVO terminalVo = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try
        {
            connection = Database.getRDBConnection();
            //query="SELECT terminalid,addressValidation,addressDetails,isEmailWhitelisted,isCardWhitelisted,is_recurring,isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid FROM member_account_mapping AS m, whitelist_bins AS b WHERE bin=? AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid";
            query="SELECT ga.merchantid,gt.pgtypeid,gt.gateway,m.isActive,terminalid,m.addressValidation,isTokenizationActive,addressDetails,isEmailWhitelisted,isCardWhitelisted,m.is_recurring,m.isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid,m.whitelisting_details,m.cardLimitCheck, m.cardAmountLimitCheck, m.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS m, whitelist_bins AS b,gateway_accounts AS ga, gateway_type AS gt WHERE ? BETWEEN CONCAT(b.startBin,b.startCard) AND CONCAT(b.endBin,b.endCard) AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid AND m.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND m.binRouting='Y'";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,cNum);
            pstmt.setString(2,memberId);
            pstmt.setString(3,paymodeid);
            pstmt.setString(4,cardtypeid);
            pstmt.setString(5,currency);

            transactionLogger.error("getBinRoutingTerminalDetailsByCardNumber---" + pstmt);

            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setMemberId(rs.getString("merchantid"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setCurrency(currency);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }
    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(String memberId,String paymodeId, String cardTypeId, String currency,String cNum) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck,mam.emi_support, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bins AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND ? BETWEEN CONCAT(b.startBin,b.startCard) AND CONCAT(b.endBin,b.endCard) AND mam.accountid=ga.accountid AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='Y'";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            ps.setString(5, cNum);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }
    public TerminalVO getAccountIdTerminalVOforBinRoutingByCardNumber(String toid, int paymenttype, int cardtype,String cNum)
    {
        String accountId="0";
        Connection con = null;
        TerminalVO terminalVO=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {
            con=Database.getRDBConnection();
            String query2 = "SELECT gt.currency,mam.accountid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.terminalid,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bins AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.isActive='Y' AND ? BETWEEN CONCAT(b.startBin,b.startCard) AND CONCAT(b.endBin,b.endCard) AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND mam.binRouting='Y'";
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            pstmt2.setString(4, cNum);
            transactionLogger.error("getAccountIdTerminalVOforBinRoutingByCardNumber pstmt2----" + pstmt2);

            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminalVO = new TerminalVO();
                accountId = rs2.getString("accountid");
                terminalVO.setMax_transaction_amount(rs2.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs2.getFloat("min_transaction_amount"));
                terminalVO.setTerminalId(rs2.getString("terminalid"));
                terminalVO.setIsActive(rs2.getString("isActive"));
                terminalVO.setAddressDetails(rs2.getString("addressDetails"));
                terminalVO.setAddressValidation(rs2.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs2.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs2.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs2.getString("isManualRecurring"));
                terminalVO.setIsPSTTerminal(rs2.getString("isPSTTerminal"));
                terminalVO.setAccountId(accountId);
                terminalVO.setCurrency(rs2.getString("currency"));
                terminalVO.setAutoRedirectRequest(rs2.getString("autoRedirectRequest"));
                terminalVO.setReject3DCard(rs2.getString("reject3DCard"));
                terminalVO.setCurrencyConversion(rs2.getString("currency_conversion"));
                terminalVO.setConversionCurrency(rs2.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs2.getString("isCardWhitelisted"));
                terminalVO.setIsEmailWhitelisted(rs2.getString("isEmailWhitelisted"));
                terminalVO.setWhitelisting(rs2.getString("whitelisting_details"));
                terminalVO.setCardLimitCheckTerminalLevel(rs2.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheckTerminalLevel(rs2.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs2.getString("amountLimitCheck"));
                terminalVO.setCardLimitCheckAccountLevel(rs2.getString("cardLimitAccountLevel"));
                terminalVO.setCardAmountLimitCheckAccountLevel(rs2.getString("cardAmountLimitCheckAcc"));
                terminalVO.setAmountLimitCheckAccountLevel(rs2.getString("amountLimitCheckAcc"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(con);
        }

        return terminalVO;
    }
    public StringBuffer getVendorsTerminalByMemberid(String memberid) throws PZDBViolationException
    {
        TerminalVO terminalVo = null;
        Connection connection=null;
        PreparedStatement p=null;
        ResultSet rs=null;
        StringBuffer terminalid=new StringBuffer();
        try
        {
            connection=Database.getRDBConnection();
            String selectStatement="SELECT DISTINCT terminalid FROM transaction_common  WHERE toid=? AND parentTrackingid IS NOT NULL  ORDER BY terminalid";
            p=connection.prepareStatement(selectStatement);
            p.setString(1,memberid);
            rs=p.executeQuery();
            logger.debug("query----"+p);

            while (rs.next())
            {
                if(rs.isLast())
                    terminalid.append(rs.getString("terminalid"));
                else
                    terminalid.append(rs.getString("terminalid")+",");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByTerminalId()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalByTerminalId()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
        return terminalid;
    }

    public TerminalVO getTerminalCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  ORDER BY mam.priority,mam.terminalid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public HashMap getListofPaymentandCurrencyMapByToid(String toid)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();
        List<String> cardList = null;
        Connection conn = null;
        Functions functions=new Functions();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT paymodeid FROM member_account_mapping AS m WHERE memberid=? AND m.isActive='Y'");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toid);

            logger.debug("query for paymentType ---" + pstmt);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                cardList = new ArrayList<String>();
                StringBuffer query1 = new StringBuffer("SELECT DISTINCT currency FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");

                PreparedStatement pstmt1 = conn.prepareStatement(query1.toString());
                pstmt1.setString(1,res.getString("paymodeid"));
                pstmt1.setString(2, toid);

                logger.debug("query1 for currency---" + pstmt1);
                ResultSet res1 = pstmt1.executeQuery();
                while (res1.next())
                {
                    cardList.add(res1.getString("currency"));
                }
                if(cardList.size()>0)
                    map.put(res.getString("paymodeid"), cardList);

                //System.out.println("paymode currency map---"+map);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("getListofPaymentandCurrencyMapByToid System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCurrencyMapByToid()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("getListofPaymentandCurrencyMapByToid throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCurrencyMapByToid()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return map;
    }


    public HashMap getListofPaymentandCardtypeByToid(String toid,String currency)throws PZDBViolationException
    {
        HashMap<String,List<String>> map = new HashMap<String, List<String>>();
        List<String> cardList = null;
        Connection conn = null;
        Functions functions=new Functions();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT paymodeid FROM member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE memberid=?  AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
            if(functions.isValueNull(currency))
                query.append(" AND currency=?");
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, toid);
            if(functions.isValueNull(currency))
                pstmt.setString(2, currency);
            logger.debug("query for paymentType ---" + pstmt);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                cardList = new ArrayList<String>();
                StringBuffer query1 = new StringBuffer("SELECT DISTINCT cardtypeid FROM member_account_mapping AS m,gateway_accounts AS ga, gateway_type AS gt WHERE paymodeid=? AND memberid=? AND ga.accountid=m.accountid AND ga.pgtypeid=gt.pgtypeid AND m.isActive='Y'");
                if(functions.isValueNull(currency))
                    query1.append(" AND currency=?");
                PreparedStatement pstmt1 = conn.prepareStatement(query1.toString());
                pstmt1.setString(1,res.getString("paymodeid"));
                pstmt1.setString(2, toid);
                if(functions.isValueNull(currency))
                    pstmt1.setString(3, currency);
                logger.debug("query1 for cardType---" + pstmt1);
                ResultSet res1 = pstmt1.executeQuery();
                while (res1.next())
                {
                    cardList.add(res1.getString("cardtypeid"));
                }
                if(cardList.size()>0)
                    map.put(res.getString("paymodeid"), cardList);
            }
        }
        catch(SystemError systemError)
        {
            logger.error("getListofPaymentandCardtypeByToid throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtypeByToid()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("getListofPaymentandCardtypeByToid throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getListofPaymentandCardtypeByToid()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return map;
    }



    public HashMap getPaymdeCardTerminalVOFromMemberId(String memberId,String currency) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        HashMap terminalHash = new HashMap();
        Functions functions = new Functions();

        try
        {
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,gt.pgtypeid,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.emi_support, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid ORDER BY mam.priority,mam.terminalid");
            if(functions.isValueNull(currency))
                qry.append(" AND gt.currency=? ");
            ps = con.prepareStatement(qry.toString());
            ps.setString(1,memberId);
            if(functions.isValueNull(currency))
                ps.setString(2,currency);

            logger.debug("getPaymdeCardTerminalVOFromMemberId ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setCurrency(rs.getString("currency"));
                    terminalVO.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")));
                    terminalVO.setCardType(GatewayAccountService.getCardType(rs.getString("cardtypeid")));
                    if(!terminalHash.containsKey(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()))
                        terminalHash.put(terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+rs.getString("currency"),terminalVO);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error getPaymdeCardTerminalVOFromMemberId = ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPaymdeCardTerminalVOFromMemberId()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error getPaymdeCardTerminalVOFromMemberId = ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPaymdeCardTerminalVOFromMemberId()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalHash;
    }
    public Map<String,Map<String,Set<String>>> getPaymentAndCardTypePerCurrency(String memberId) throws PZDBViolationException
    {
        Map<String,Map<String,Set<String>>> hashMap=new HashMap<>();
        Map<String,Set<String>> paymentType=null;
        Set<String> cardType=null;
        List<String> paymodidList =new ArrayList<>();
        List<String> currencyList =new ArrayList<>();
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            con=Database.getConnection();
            String query = "SELECT mam.paymodeid,mam.cardtypeid,gt.currency,mam.addressValidation FROM member_account_mapping AS mam,gateway_accounts AS ga,gateway_type AS gt WHERE mam.accountId=ga.accountId AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=? AND mam.isActive='Y' ORDER BY gt.currency,mam.paymodeid";
            ps = con.prepareStatement(query);
            ps.setString(1,memberId);
            transactionLogger.error("getPaymentAndCardTypePerCurrency---->"+ps);
            rs=ps.executeQuery();
            while(rs.next())
            {
                if(!currencyList.contains(rs.getString("currency")))
                {
                    currencyList.add(rs.getString("currency"));
                    paymentType = new HashMap<>();
                    paymodidList=new ArrayList<>();
                    if (!paymodidList.contains(rs.getString("paymodeid")))
                    {
                        cardType = new HashSet();
                        paymodidList.add(rs.getString("paymodeid"));
                        cardType.add(GatewayAccountService.getPaymentBrand(rs.getString("cardtypeid"))+":"+rs.getString("addressValidation"));
                    }
                    else
                    {
                        cardType.add(GatewayAccountService.getPaymentBrand(rs.getString("cardtypeid"))+":"+rs.getString("addressValidation"));
                    }
                    paymentType.put(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")), cardType);
                }else {
                    if (!paymodidList.contains(rs.getString("paymodeid")))
                    {
                        cardType = new HashSet();
                        paymodidList.add(rs.getString("paymodeid"));
                        cardType.add(GatewayAccountService.getPaymentBrand(rs.getString("cardtypeid"))+":"+rs.getString("addressValidation"));
                    }
                    else
                    {
                        cardType.add(GatewayAccountService.getPaymentBrand(rs.getString("cardtypeid"))+":"+rs.getString("addressValidation"));
                    }
                    paymentType.put(GatewayAccountService.getPaymentMode(rs.getString("paymodeid")), cardType);
                }
                hashMap.put(rs.getString("currency"),paymentType);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error getPaymdeCardTerminalVOFromMemberId = ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPaymentAndCardTypePerCurrency()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Error getPaymdeCardTerminalVOFromMemberId = ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getPaymentAndCardTypePerCurrency()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return hashMap;
    }
    public List<TerminalVO> getPartnerGatewayMappingList(String pgtypeId)throws Exception
    {
        List<TerminalVO> gatewayList=new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        TerminalVO terminalVO=null;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT g.partnerid as partnerid,p.partnerName as partnerName FROM gatewaytype_partner_mapping AS g JOIN partners AS p WHERE p.`partnerId`=g.`partnerid` AND pgtypeid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,pgtypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                terminalVO=new TerminalVO();
                terminalVO.setPartnerId(rs.getString("partnerid"));
                terminalVO.setPartnerName(rs.getString("partnerName"));
                gatewayList.add(terminalVO);
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return gatewayList;
    }
    public TerminalVO getBinCountryRoutingTerminalDetails(String country,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        TerminalVO terminalVo = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try
        {
            connection = Database.getRDBConnection();
            //query="SELECT terminalid,addressValidation,addressDetails,isEmailWhitelisted,isCardWhitelisted,is_recurring,isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid FROM member_account_mapping AS m, whitelist_bins AS b WHERE bin=? AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid";
            query="SELECT ga.merchantid,gt.pgtypeid,gt.gateway,m.isActive,terminalid,m.addressValidation,isTokenizationActive,addressDetails,isEmailWhitelisted,isCardWhitelisted,m.is_recurring,m.isManualRecurring,isPSTTerminal,reject3DCard,currency_conversion,conversion_currency,m.accountid,m.whitelisting_details,m.cardLimitCheck, m.cardAmountLimitCheck, m.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS m, whitelist_bin_country AS b,gateway_accounts AS ga, gateway_type AS gt WHERE b.country=? AND b.memberId=? AND paymodeid=? AND cardtypeid=? AND currency=? AND m.accountid=b.accountid AND m.memberid=b.memberid AND m.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND m.binRouting='Y'";
            pstmt=connection.prepareStatement(query);
            pstmt.setString(1,country);
            pstmt.setString(2,memberId);
            pstmt.setString(3,paymodeid);
            pstmt.setString(4,cardtypeid);
            pstmt.setString(5,currency);

            transactionLogger.error("getBinCountryRoutingTerminalDetails---" + pstmt);

            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setMemberId(rs.getString("merchantid"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setCurrency(currency);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid", systemError);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid", e);
            //PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getBinRoutingTerminalDetails()", null, "Common", "Sql Exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }
    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(String memberId,String paymodeId, String cardTypeId, String currency,String country) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck,mam.emi_support, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bin_country AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND gt.currency=? AND b.country=? AND mam.accountid=ga.accountid AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND ga.pgtypeid=gt.pgtypeid AND mam.binRouting='Y'";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,paymodeId);
            ps.setString(3,cardTypeId);
            ps.setString(4, currency);
            ps.setString(5, country);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }
    public TerminalVO getAccountIdTerminalVOforBinCountryRouting(String toid, int paymenttype, int cardtype,String country)
    {
        String accountId="0";
        Connection con = null;
        TerminalVO terminalVO=null;
        PreparedStatement pstmt2=null;
        ResultSet rs2=null;
        try
        {
            con=Database.getRDBConnection();
            String query2 = "SELECT gt.currency,mam.accountid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.terminalid,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.whitelisting_details, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,whitelist_bin_country AS b WHERE mam.memberid=? AND mam.paymodeid=? AND mam.cardtypeid=? AND mam.isActive='Y' AND b.country=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' AND mam.accountid=b.accountid AND mam.memberid=b.memberid AND mam.binRouting='Y'";
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            pstmt2.setString(4, country);
            transactionLogger.error("getAccountIdTerminalVOforBinRoutingByCountry pstmt2----" + pstmt2);

            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminalVO = new TerminalVO();
                accountId = rs2.getString("accountid");
                terminalVO.setMax_transaction_amount(rs2.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs2.getFloat("min_transaction_amount"));
                terminalVO.setTerminalId(rs2.getString("terminalid"));
                terminalVO.setIsActive(rs2.getString("isActive"));
                terminalVO.setAddressDetails(rs2.getString("addressDetails"));
                terminalVO.setAddressValidation(rs2.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs2.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs2.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs2.getString("isManualRecurring"));
                terminalVO.setIsPSTTerminal(rs2.getString("isPSTTerminal"));
                terminalVO.setAccountId(accountId);
                terminalVO.setCurrency(rs2.getString("currency"));
                terminalVO.setAutoRedirectRequest(rs2.getString("autoRedirectRequest"));
                terminalVO.setReject3DCard(rs2.getString("reject3DCard"));
                terminalVO.setCurrencyConversion(rs2.getString("currency_conversion"));
                terminalVO.setConversionCurrency(rs2.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs2.getString("isCardWhitelisted"));
                terminalVO.setIsEmailWhitelisted(rs2.getString("isEmailWhitelisted"));
                terminalVO.setWhitelisting(rs2.getString("whitelisting_details"));
                terminalVO.setCardLimitCheckTerminalLevel(rs2.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheckTerminalLevel(rs2.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs2.getString("amountLimitCheck"));
                terminalVO.setCardLimitCheckAccountLevel(rs2.getString("cardLimitAccountLevel"));
                terminalVO.setCardAmountLimitCheckAccountLevel(rs2.getString("cardAmountLimitCheckAcc"));
                terminalVO.setAmountLimitCheckAccountLevel(rs2.getString("amountLimitCheckAcc"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(con);
        }

        return terminalVO;
    }
    public TerminalVO getMemberUserTerminalDetails(String terminalid, String memberId, String currency,String userid) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query = new StringBuffer();
        try
        {
            connection=Database.getConnection();
            query.append("SELECT gt.currency,mam.addressValidation,mam.addressDetails,mam.isTokenizationActive,gt.gateway,mam.isManualRecurring,mam.is_recurring,mam.memberid,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount \n" +
                    "FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt ,member_user_account_mapping AS muam WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND muam.terminalid=mam.terminalid AND muam.terminalid =? AND muam.memberid=? AND muam.userid=? AND currency=?");
            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1,terminalid);
            pstmt.setString(2,memberId);
            pstmt.setString(3,userid);
            pstmt.setString(4,currency);
            logger.debug("qury-->" + pstmt);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setGateway(rs.getString("gateway"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalVo;
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandMemberUser(String memberId,String paymodeId, String cardTypeId, String currency,String userid) throws PZDBViolationException
    {
        TerminalVO terminalVO = null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getConnection();
            String qry = "SELECT mam.max_transaction_amount,mam.min_transaction_amount,mam.terminalid,ga.merchantid,ga.pgtypeid,mam.paymodeid, mam.cardtypeid,gt.currency,mam.cardDetailRequired,mam.addressDetails,mam.addressValidation,mam.is_recurring,mam.isManualRecurring,gt.gateway,mam.isTokenizationActive,mam.isActive,mam.accountid, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid,mam.emi_support FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt,member_user_account_mapping AS muam WHERE muam.memberid=? AND muam.userid=? AND muam.paymodeid=? AND muam.cardtypeid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND muam.terminalid=mam.terminalid AND mam.binRouting='N' ORDER BY mam.priority,mam.terminalid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,userid);
            ps.setString(3,paymodeId);
            ps.setString(4,cardTypeId);
            ps.setString(5, currency);
            logger.debug("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setPaymodeId(rs.getString("paymodeid"));
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));
                    terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                    terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                    terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                    terminalVO.setIsRecurring(rs.getString("is_recurring"));
                    terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                    terminalVO.setMemberId(rs.getString("merchantid"));
                    terminalVO.setGateway(rs.getString("gateway"));
                    terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                    terminalVO.setAddressDetails(rs.getString("addressDetails"));
                    terminalVO.setAddressValidation(rs.getString("addressValidation"));
                    terminalVO.setIsActive(rs.getString("isActive"));
                    terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                    terminalVO.setReject3DCard(rs.getString("reject3DCard"));
                    terminalVO.setCurrencyConversion(rs.getString("currency_conversion"));
                    terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                    terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                    terminalVO.setWhitelisting(rs.getString("whitelisting_details"));
                    terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                    terminalVO.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                    terminalVO.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                    terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                    terminalVO.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                    terminalVO.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                    terminalVO.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                    terminalVO.setGateway_id(rs.getString("pgtypeid"));
                    terminalVO.setIsEmi_support(rs.getString("emi_support"));
                    terminalVO.setCurrency(currency);
                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }

    public boolean isGatewayCurrencyExistWithMemberUser(String memberId, String currency,String userid) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        boolean isGatewayCurrencyExistWithMember = false;
        try
        {
            con = Database.getConnection();
            String qry = "SELECT gt.currency FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt ,member_user_account_mapping AS muam\n" +
                    "WHERE muam.memberid=? AND muam.userid=? AND gt.currency=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND muam.terminalid=mam.terminalid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,userid);
            ps.setString(3, currency);
            logger.debug("isGatewayCurrencyExistWithMemberUser ps----" + ps);
            rs = ps.executeQuery();
            if(rs.next())
            {
                isGatewayCurrencyExistWithMember = true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while finding isGatewayCurrencyExistWithMember",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "isGatewayCurrencyExistWithMember()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while finding isGatewayCurrencyExistWithMember",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "isGatewayCurrencyExistWithMember()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return isGatewayCurrencyExistWithMember;
    }
    public TerminalVO getRoutingTerminalByFromAccountId(String fromAccountId,String memberId,String paymodeId,String cardTypeId) throws PZDBViolationException
    {
        TerminalVO terminalVO=null;
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;

        try
        {
            con = Database.getConnection();
            String qry = "SELECT terminalid,accountid,paymodeid, cardtypeid,isActive FROM member_account_mapping  WHERE memberid=? AND accountid=?  AND paymodeid=? AND cardtypeid=?  ORDER BY priority,terminalid";
            ps = con.prepareStatement(qry);
            ps.setString(1,memberId);
            ps.setString(2,fromAccountId);
            ps.setString(3,paymodeId);
            ps.setString(4,cardTypeId);
            transactionLogger.error("getCardIdAndPaymodeIdFromPaymentBrand ps----" + ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                if("Y".equals(rs.getString("isActive")))
                {
                    terminalVO = new TerminalVO();
                    terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                    terminalVO.setPaymodeId(rs.getString("cardtypeid"));
                    terminalVO.setAccountId(rs.getString("accountid"));
                    terminalVO.setTerminalId(rs.getString("terminalid"));

                    return terminalVO;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collecting paymodeid and cardtypeid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVO;
    }
    public String getPreviousWireDetails(String agentId)
    {
        Connection con = null;
        ResultSet rsSingleBankWireManager = null;
        PreparedStatement preparedStatement=null;
        String endDate="";
        try
        {
            con = Database.getRDBConnection();
            String query = "SELECT settlementenddate from agent_wiremanager where agentid=? ORDER BY settledid DESC ";
            preparedStatement = con.prepareStatement(query.toString());
            preparedStatement.setString(1, agentId);
            rsSingleBankWireManager = preparedStatement.executeQuery();
            logger.error("query----"+preparedStatement);
            if (rsSingleBankWireManager.next())
            {
                endDate=rsSingleBankWireManager.getString("settlementenddate");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error for getSingleBankSettlementCycleActionSpecific::", systemError);
        }
        catch (SQLException e)
        {
            logger.error(" SQL Exception for getSingleBankSettlementCycleActionSpecific::", e);
        }
        finally
        {
            Database.closeResultSet(rsSingleBankWireManager);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return endDate;
    }



    public  LinkedList<TerminalVO> getPayoutActiveTerminal(String memberId) throws PZDBViolationException
    {
        TerminalVO terminalVo=null;
        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        LinkedList<TerminalVO> terminalLinkedlist =new LinkedList();
        StringBuffer query=null;
        Functions functions=new Functions();
        try
        {
            connection=Database.getRDBConnection();
            query = new StringBuffer("SELECT gt.currency,memberid,mam.accountid,paymodeid,cardtypeid,terminalid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,addressDetails,mam.addressValidation,cardDetailRequired,mam.is_recurring,isManualRecurring,isPSTTerminal,isTokenizationActive, payoutActivation, mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted,mam.whitelisting_details,mam.cardLimitCheck,mam.cardAmountLimitCheck,mam.amountLimitCheck,ga.cardLimitCheck AS cardLimitAccountLevel,ga.cardAmountLimitCheckAcc,ga.amountLimitCheckAcc,gt.pgtypeid FROM member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt WHERE payoutActivation='Y' AND memberid=? AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' order by payout_priority");

            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1, memberId);
            rs=pstmt.executeQuery();
            while (rs.next())
            {
                terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setTerminalId(rs.getString("terminalid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setCardTypeId(rs.getString("cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("paymodeid"));
                terminalVo.setIsActive(rs.getString("isActive"));
                terminalVo.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVo.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVo.setAddressDetails(rs.getString("addressDetails"));
                terminalVo.setAddressValidation(rs.getString("addressValidation"));
                terminalVo.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVo.setIsRecurring(rs.getString("is_recurring"));
                terminalVo.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVo.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVo.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVo.setCurrency(rs.getString("currency"));
                terminalVo.setPayoutActivation(rs.getString("payoutActivation"));
                terminalVo.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                terminalVo.setReject3DCard(rs.getString("reject3DCard"));
                terminalVo.setCurrencyConversion(rs.getString("currency_conversion"));
                terminalVo.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVo.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVo.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVo.setWhitelisting(rs.getString("whitelisting_details"));
                terminalVo.setCardLimitCheckTerminalLevel(rs.getString("cardLimitCheck"));
                terminalVo.setCardAmountLimitCheckTerminalLevel(rs.getString("cardAmountLimitCheck"));
                terminalVo.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVo.setCardLimitCheckAccountLevel(rs.getString("cardLimitAccountLevel"));
                terminalVo.setCardAmountLimitCheckAccountLevel(rs.getString("cardAmountLimitCheckAcc"));
                terminalVo.setAmountLimitCheckAccountLevel(rs.getString("amountLimitCheckAcc"));
                terminalVo.setGateway_id(rs.getString("pgtypeid"));
                terminalLinkedlist.add(terminalVo);
            }
            logger.debug("query in getPayoutActiveTerminal for token with merchant----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in getPayoutActiveTerminal while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception in getPayoutActiveTerminal while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return terminalLinkedlist;
    }


    public   Map<String,HashMap> getPayoutAmountLimit() throws PZDBViolationException
    {
        Map<String,HashMap> mainHashMap =new HashMap<>();

        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query=null;

        try
        {
            connection=Database.getRDBConnection();
            query = new StringBuffer("SELECT * from payout_amount_limit");
            pstmt=connection.prepareStatement(query.toString());
            rs=pstmt.executeQuery();
            while (rs.next())
            {
                HashMap <String,String> innerMap=new HashMap<>();
                innerMap.put("accountid",rs.getString("accountid"));
                innerMap.put("currentPayoutAmount",rs.getString("currentPayoutAmount"));
                innerMap.put("addedPayoutAmount",rs.getString("addedPayoutAmount"));

                mainHashMap.put(rs.getString("accountid"),innerMap);
            }
            logger.debug("query getPayoutAmountLimit----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in getPayoutAmountLimit while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception in getPayoutAmountLimit while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return mainHashMap;

    }


    public   Map<String,String> getPayoutAmountLimitByAccountid(String accountid) throws PZDBViolationException
    {
        Map<String,String> mainHashMap =null;

        Connection connection=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer query=null;

        try
        {
            connection=Database.getRDBConnection();
            query = new StringBuffer("SELECT * from payout_amount_limit where accountid=?");

            pstmt=connection.prepareStatement(query.toString());
            pstmt.setString(1, accountid);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                mainHashMap=new HashMap<>();
                mainHashMap.put("accountid",rs.getString("accountid"));
                mainHashMap.put("currentPayoutAmount",rs.getString("currentPayoutAmount"));
                mainHashMap.put("addedPayoutAmount",rs.getString("addedPayoutAmount"));

            }
            logger.debug("query getPayoutAmountLimitByAccountid----"+pstmt);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in getPayoutAmountLimitByAccountid while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","System error while Connecting to member_account_mapping",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception in getPayoutAmountLimitByAccountid while collect terminalid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getTerminalByTerminalId()",null,"Common","Sql Exception due to incorrect query to member_account_mapping",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return mainHashMap;
    }

    public Boolean updatePayoutTransactionAmount (String accountid, String  currentPayoutAmount ){

        logger.error("in side  update Payout Transaction amount----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update payout_amount_limit set currentPayoutAmount= ? where accountid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, currentPayoutAmount);
            psUpdateTransaction.setString(2, accountid);
            logger.error("updatePayoutTransactionAmount query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            logger.error("SQLException in  updatePayoutTransactionAmount----",e);

        }
        catch (SystemError systemError)
        {
            logger.error(" systemError in updatePayoutTransactionAmount", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }
    public List<TerminalVO> getTerminalsByAccAndBankId(String accountId, String parent_bankwireid,String cardtypeid,String paymodid) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        StringBuffer selectStatement =new StringBuffer();
        Functions functions = new Functions();
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            //**vimp note :::it should be order by member id first only.
            selectStatement.append("SELECT m.memberid, m.accountid,m.paymodeid,m.cardtypeid,m.terminalid,m.settlement_currency,b.bankwiremanagerId, b.parent_bankwireid FROM member_account_mapping AS m JOIN bank_wiremanager AS b WHERE m.accountid=b.accountid AND b.issettlementcronexceuted='Y' AND b.ispayoutcronexcuted='N' AND m.accountid IN("+accountId+") AND (b.bankwiremanagerId='"+parent_bankwireid+"' or b.parent_bankwireid='"+parent_bankwireid+"')");
            if (functions.isValueNull(cardtypeid)){
                selectStatement.append(" and m.cardtypeid= '"+cardtypeid+"'");
            }
            if (functions.isValueNull(paymodid)){
                selectStatement.append(" and m.paymodeid= '"+paymodid+"'");
            }
            selectStatement.append("  ORDER BY m.memberid");

            preparedStatement=connection.prepareStatement(selectStatement.toString());
//            preparedStatement.setString(1,parent_bankwireid);
//            preparedStatement.setString(2,parent_bankwireid);
            rs=preparedStatement.executeQuery();
            logger.error("getTerminalsByAccountID-------------------"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("m.memberid"));
                terminalVo.setTerminalId(rs.getString("m.terminalid"));
                terminalVo.setAccountId(rs.getString("m.accountid"));
                terminalVo.setCardType(GatewayAccountService.getCardType(rs.getString("m.cardtypeid")));
                terminalVo.setPaymentName(GatewayAccountService.getPaymentMode(rs.getString("m.paymodeid")));
                terminalVo.setPaymentTypeName(GatewayAccountService.getPaymentTypes(rs.getString("m.paymodeid")));
                terminalVo.setCardTypeId(rs.getString("m.cardtypeid"));
                terminalVo.setPaymodeId(rs.getString("m.paymodeid"));
                terminalVo.setSettlementCurrency(rs.getString("m.settlement_currency"));
                if (rs.getString("b.bankwiremanagerId")!=null)
                { terminalVo.setWireId(rs.getString("b.bankwiremanagerId"));}
                if (rs.getString("b.parent_bankwireid")!=null)
                { terminalVo.setParentBankWireId(rs.getString("b.parent_bankwireid")); }
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }
    public List<TerminalVO> getGenratedReportByAccountID(String accountId) throws PZDBViolationException
    {
        List<TerminalVO> accountList=new ArrayList();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        String selectStatement ="";
        ResultSet rs=null;
        try
        {
            connection=Database.getRDBConnection();
            //**vimp note :::it should be order by member id first only.
            selectStatement="SELECT memberid, accountid, cycleid, parentcycleid FROM bank_merchant_settlement_master WHERE accountid =? ORDER BY memberid";
            preparedStatement=connection.prepareStatement(selectStatement);
            preparedStatement.setString(1, accountId);
            rs=preparedStatement.executeQuery();
            logger.error("getGenratedReportByAccountID-------------------"+preparedStatement);
            while (rs.next())
            {
                TerminalVO terminalVo=new TerminalVO();
                terminalVo.setMemberId(rs.getString("memberid"));
                terminalVo.setAccountId(rs.getString("accountid"));
                terminalVo.setWireId(rs.getString("cycleid"));
                terminalVo.setParentBankWireId(rs.getString("parentcycleid"));
                accountList.add(terminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "Sql exception due to incorrect query to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
//            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getTerminalsByAccountID()", null, "Common", "System error while Connecting to member_account_mapping", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public List<TerminalVO> getTerminalsFlagsByMerchantId(String memberid ,String paymodeid ,String cardtypeid ,String currency ) throws PZDBViolationException
    {

        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        List <TerminalVO> terminalVOList=new ArrayList();;
        Functions functions=new Functions();
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("select memberid,accountid,paymodeid,cardtypeid,monthly_amount_limit,daily_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isTest,terminalid,weekly_amount_limit,is_recurring,isRestrictedTicketActive,isTokenizationActive,isManualRecurring,addressDetails,addressValidation,cardDetailRequired,isPSTTerminal,isCardEncryptionEnable,riskruleactivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket,settlement_currency,min_payout_amount,payoutActivation,autoRedirectRequest,isCardWhitelisted,isEmailWhitelisted,currency_conversion,conversion_currency,binRouting,emi_support,whitelisting_details,cardLimitCheck,cardAmountLimitCheck,amountLimitCheck,actionExecutorId,actionExecutorName,processor_partnerid,payout_priority,daily_amount_limit_check,weekly_amount_limit_check,monthly_amount_limit_check,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,daily_card_amount_limit_check,weekly_card_amount_limit_check,monthly_card_amount_limit_check from member_account_mapping as M where accountid>0 ");
            String ActiveOrInActive="Y";
            String PayoutActiveorInactive="Y";

            if (functions.isValueNull(memberid))
            {
                query.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
              //  countquery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, currency));
              //  countquery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, currency));
            }
            if (functions.isValueNull(paymodeid))
            {
                query.append(" and paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
               // countquery.append(" and paymodeid=" +ESAPI.encoder().encodeForSQL(me, paymodeid));
            }
            if (functions.isValueNull(cardtypeid))
            {
                query.append(" and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardtypeid));
               // countquery.append(" and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardTypeId));
            }

            if (!functions.isEmptyOrNull(ActiveOrInActive)){
                query.append(" and isActive='" + ESAPI.encoder().encodeForSQL(me, ActiveOrInActive) +"'");
            }
            if (!functions.isEmptyOrNull(PayoutActiveorInactive))
            {
                query.append(" and payoutActivation='" + ESAPI.encoder().encodeForSQL(me, PayoutActiveorInactive) + "'");
            }
            query.append(" order by memberid ");
            logger.error("---query---" + query);
            con = Database.getRDBConnection();
            rs=Database.executeQuery(query.toString(),con);
            while(rs.next())
            {

                TerminalVO terminalVO = new TerminalVO();
                terminalVO.setMemberId(rs.getString("memberid"));
                terminalVO.setPaymodeId(rs.getString("paymodeid"));
                terminalVO.setCardTypeId(rs.getString("cardtypeid"));
                terminalVO.setAccountId(rs.getString("accountid"));
                terminalVO.setTerminalId(rs.getString("terminalid"));
                terminalVO.setIsActive(rs.getString("isActive"));
                terminalVO.setPriority(rs.getString("priority"));
                terminalVO.setIsTest(rs.getString("isTest"));
                terminalVO.setIsRecurring(rs.getString("is_recurring"));
                terminalVO.setIsRestrictedTicketActive(rs.getString("isRestrictedTicketActive"));
                terminalVO.setIsTokenizationActive(rs.getString("isTokenizationActive"));
                terminalVO.setIsManualRecurring(rs.getString("isManualRecurring"));
                terminalVO.setAddressDetails(rs.getString("addressDetails"));
                terminalVO.setAddressValidation(rs.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs.getString("cardDetailRequired"));
                terminalVO.setIsPSTTerminal(rs.getString("isPSTTerminal"));
                terminalVO.setIsCardEncryptionEnable(rs.getString("isCardEncryptionEnable"));
                terminalVO.setRiskRuleActivation(rs.getString("riskruleactivation"));
                terminalVO.setIsEmailWhitelisted(rs.getString("isEmailWhitelisted"));
                terminalVO.setPayoutActivation(rs.getString("payoutActivation"));
                terminalVO.setSettlementCurrency(rs.getString("settlement_currency"));
                terminalVO.setAutoRedirectRequest(rs.getString("autoRedirectRequest"));
                terminalVO.setConversionCurrency(rs.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs.getString("isCardWhitelisted"));
                terminalVO.setIsbin_routing(rs.getString("binRouting"));
                terminalVO.setIsEmi_support(rs.getString("emi_support"));
                terminalVO.setCardLimitCheck(rs.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheck(rs.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs.getString("amountLimitCheck"));
                terminalVO.setPayout_priority(rs.getString("payout_priority"));
                terminalVO.setDaily_amount_limit_check(rs.getString("daily_card_amount_limit_check"));
                terminalVO.setWeekly_amount_limit_check(rs.getString("weekly_card_amount_limit_check"));
                terminalVO.setMonthly_amount_limit_check(rs.getString("monthly_card_amount_limit_check"));
               /* terminalVO.setDaily_card_amount_limit_check(rs.getString("Daily_Card_Amount_Limit_chk"));
                terminalVO.setWeekly_card_amount_limit_check(rs.getString("Weekly_Card_Amount_Limit_chk"));
                terminalVO.setMonthly_amount_limit_check(rs.getString("Monthly_Card_Amount_Limit_chk"));
                terminalVO.setDaily_card_limit_check(rs.getString("Daily_Card_Limit_chk"));
                terminalVO.setWeekly_card_limit_check(rs.getString("Weekly_Card_Limit_chk"));
                terminalVO.setMonthly_card_limit_check(rs.getString("Monthly_Card_Limit_chk"));*/
                terminalVO.setMax_transaction_amount(rs.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs.getFloat("min_transaction_amount"));
                terminalVOList.add(terminalVO);
                }
        }

        catch (SQLException e)
        {
            logger.error("System Error while collecting  paymodeid and cardtypeid",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getCardIdAndPaymodeIdFromPaymentBrand()", null, "Common", "System error while Connecting to member_account_mapping and members", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return terminalVOList;
    }
}
