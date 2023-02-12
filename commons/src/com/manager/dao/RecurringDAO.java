package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.PaginationVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/27/15
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecurringDAO
{
    private static Logger log = new Logger(RecurringDAO.class.getName());
    private static Functions functions = new Functions();

    public int insertRecurringTransactionEntry(RecurringBillingVO recurringBillingVO) throws PZDBViolationException
    {
        int recurring_trans_detailID = 0;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String insertQuert = "insert into recurring_transaction_details(recurring_subscription_id,bankRecurringBillingID,parentBankTransactionID,newBankTransactionID,parentPzTransactionID,newPzTransactionID,amount,description,transactionStatus,recurringRunDate,dtstamp) values (?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement ps = conn.prepareStatement(insertQuert);
            ps.setString(1,recurringBillingVO.getRecurring_subscrition_id());
            ps.setString(2,recurringBillingVO.getBankRecurringBillingID());
            ps.setString(3,recurringBillingVO.getParentBankTransactionID());
            ps.setString(4,recurringBillingVO.getNewBankTransactionID());
            ps.setString(5,recurringBillingVO.getParentPzTransactionID());
            ps.setString(6,recurringBillingVO.getNewPzTransactionID());
            ps.setString(7,recurringBillingVO.getAmount());
            ps.setString(8,recurringBillingVO.getDescription());
            ps.setString(9,recurringBillingVO.getTransactionStatus());
            ps.setString(10,recurringBillingVO.getRecurringRunDate());

            int num = ps.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = ps.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        recurring_trans_detailID = rs.getInt(1);
                    }
                }
            }
            log.debug("insert recurring_transaction_details query---"+insertQuert+"---recurring_trans_detailID---"+recurring_trans_detailID);
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO","insertRecurringTransactionEntry()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO","insertRecurringTransactionEntry()",null,"Common","Error while Connection with Database:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }finally {
            Database.closeConnection(conn);
        }
        return recurring_trans_detailID;
    }

    public int insertRecurringSubscriptionDetails(RecurringBillingVO recurringBillingVO) throws PZDBViolationException
    {
        int recurring_subID = 0;
        Connection conn = null;
        log.error("First Six RecurringDAO----"+recurringBillingVO.getFirstSix());
        log.error("Last Four RecurringDAO----"+recurringBillingVO.getLastFour());
        try
        {
            conn = Database.getConnection();
            String insertQuert = "insert into recurring_transaction_subscription(originatingTrackingid,rec_interval,rec_frequency,rec_runDate,amount,card_holder_name,memberid,recurring_status,dtstamp,recurringType,terminalid,iban,bic,paymodeid,cardtypeid,first_six,last_four,accountNumber,routingNumber,accountType) values (?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insertQuert);

            ps.setString(1,recurringBillingVO.getOriginTrackingId());
            ps.setString(2,recurringBillingVO.getInterval());
            ps.setString(3,recurringBillingVO.getFrequency());
            ps.setString(4,recurringBillingVO.getRunDate());
            ps.setString(5,recurringBillingVO.getAmount());
            ps.setString(6,recurringBillingVO.getCardHolderName());
            ps.setString(7,recurringBillingVO.getMemberId());
            ps.setString(8,"Activated");
            ps.setString(9,recurringBillingVO.getRecurringType());
            ps.setString(10,recurringBillingVO.getTerminalid());
            ps.setString(11,recurringBillingVO.getIBAN());
            ps.setString(12,recurringBillingVO.getBIC());
            ps.setString(13,recurringBillingVO.getPaymentType());
            ps.setString(14,recurringBillingVO.getCardType());
            ps.setString(15,recurringBillingVO.getFirstSix());
            ps.setString(16,recurringBillingVO.getLastFour());
            ps.setString(17,recurringBillingVO.getAccountNumber());
            ps.setString(18,recurringBillingVO.getRoutingNumber());
            ps.setString(19,recurringBillingVO.getAccountType());

            int num = ps.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = ps.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        recurring_subID = rs.getInt(1);
                    }
                }
            }
            log.debug("insert query insertRecurringSubscriptionDetails---"+ps);
        }
        catch (SQLException se)
        {
            log.error("insertRecurringSubscriptionDetails exception---", se);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO","insertRecurringSubscriptionDetails()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        catch (SystemError e)
        {
            log.error("insertRecurringSubscriptionDetails exception---",e);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO","insertRecurringSubscriptionDetails()",null,"Common","Error while Connection with Database:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(conn);
        }
        return recurring_subID;
    }

    public RecurringBillingVO getRecurringSubscriptionDetails(String trackingid) throws PZDBViolationException
    {
        Connection conn = null;
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        try
        {
           // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            String query = "select recurring_subscription_id,recurringType,recurring_status,original_banktransaction_id from recurring_transaction_subscription where originatingTrackingid=?";

            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,trackingid);
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                recurringBillingVO.setRecurring_subscrition_id(rs.getString("recurring_subscription_id"));
                recurringBillingVO.setRecurringType(rs.getString("recurringType"));
                recurringBillingVO.setRecurringStatus(rs.getString("recurring_status"));
                recurringBillingVO.setOriginalBankTransactionId(rs.getString("original_banktransaction_id"));
            }
            log.debug("recurring query----"+p);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(conn);
        }
        return recurringBillingVO;
    }

    public RecurringBillingVO getRecurringSubscriptionDetailsForRepeatedRecurring(String trackingid) throws PZDBViolationException
    {
        Connection conn = null;
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        try
        {
            conn = Database.getConnection();
            String query = "select recurring_subscription_id,recurringType,recurring_status,terminalid,paymodeid,cardtypeid from recurring_transaction_subscription where originatingTrackingid=?";

            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,trackingid);
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                recurringBillingVO.setRecurring_subscrition_id(rs.getString("recurring_subscription_id"));
                recurringBillingVO.setRecurringType(rs.getString("recurringType"));
                recurringBillingVO.setRecurringStatus(rs.getString("recurring_status"));
                recurringBillingVO.setPaymentType(rs.getString("paymodeid"));
                recurringBillingVO.setCardType(rs.getString("cardtypeid"));
                recurringBillingVO.setTerminalid(rs.getString("terminalid"));
            }
            log.debug("query----"+p);
            log.debug("terminal id----"+recurringBillingVO.getTerminalid());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(conn);
        }
        return recurringBillingVO;
    }


    public RecurringBillingVO getRecurringSubscriptionDetailsfromRBID(String rbid) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            //String query = "select * from recurring_transaction_subscription where rbid=?";
            String query = "select * from recurring_transaction_subscription where originatingTrackingid=?";
            p = conn.prepareStatement(query);
            p.setString(1,rbid);
            rs = p.executeQuery();
            if(rs.next())
            {
                recurringBillingVO.setRecurring_subscrition_id(rs.getString("recurring_subscription_id"));
                recurringBillingVO.setOriginTrackingId(rs.getString("originatingTrackingid"));
                recurringBillingVO.setInterval(rs.getString("rec_interval"));
                recurringBillingVO.setFrequency(rs.getString("rec_frequency"));
                recurringBillingVO.setRunDate(rs.getString("rec_runDate"));
                recurringBillingVO.setRecurringStatus(rs.getString("recurring_status"));
                recurringBillingVO.setRbid(rs.getString("rbid"));
                recurringBillingVO.setAmount(rs.getString("amount"));
                recurringBillingVO.setFirstSix(rs.getString("first_six"));
                recurringBillingVO.setLastFour(rs.getString("last_four"));
                recurringBillingVO.setCardHolderName(rs.getString("card_holder_name"));
                recurringBillingVO.setTerminalid(rs.getString("terminalid"));
                recurringBillingVO.setRecurringRegisterDate(rs.getString("timestamp"));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }
        return recurringBillingVO;
    }

    public List<RecurringBillingVO> getRecurringTransactionDetailsfromRBID(String rbid) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        List<RecurringBillingVO> listOfTransactionVo=new ArrayList<RecurringBillingVO>();
        try
        {
            conn=Database.getRDBConnection();
            //String query = "select * from recurring_transaction_details where bankRecurringBillingID=?";
            String query = "select * from recurring_transaction_details where parentPzTransactionID=?";
            p = conn.prepareStatement(query);
            p.setString(1,rbid);
            log.debug("query for recurring_transaction_details-----"+p);
            rs = p.executeQuery();
            while(rs.next())
            {
                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                recurringBillingVO.setParentBankTransactionID(rs.getString("parentBankTransactionID"));
                recurringBillingVO.setNewPzTransactionID(rs.getString("newPzTransactionID"));
                recurringBillingVO.setTransactionStatus(rs.getString("transactionStatus"));
                recurringBillingVO.setAmount(rs.getString("amount"));
                recurringBillingVO.setRecurringRunDate(rs.getString("recurringRunDate"));
                listOfTransactionVo.add(recurringBillingVO);

            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }
        return listOfTransactionVo;
    }

    public List<RecurringBillingVO> getRecurringSubscriptionDetails(RecurringBillingVO recurringBillingVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        int records=15;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index


        StringBuilder sb = new StringBuilder();
        List<RecurringBillingVO> listOfVo=new ArrayList<RecurringBillingVO>();

        try
        {
            start = (pageno - 1) * records;
            end = records;

            conn = Database.getConnection();
            sb.append("select * from recurring_transaction_subscription where ");
            StringBuilder countquery = new StringBuilder("select count(*) from recurring_transaction_subscription where ");

            if(functions.isValueNull(recurringBillingVO.getOriginTrackingId()))
            {
                sb.append(" originatingTrackingid="+recurringBillingVO.getOriginTrackingId() +" AND");
                countquery.append("originatingTrackingid="+recurringBillingVO.getOriginTrackingId()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getRbid()))
            {
                sb.append(" recurring_subscription_id="+recurringBillingVO.getRbid()+" AND");
                countquery.append("recurring_subscription_id="+recurringBillingVO.getRbid()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getFirstSix()))
            {
                sb.append(" first_six="+recurringBillingVO.getFirstSix()+" AND");
                countquery.append("first_six="+recurringBillingVO.getFirstSix()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getLastFour()))
            {
                sb.append(" last_four="+recurringBillingVO.getLastFour()+" AND");
                countquery.append("last_four="+recurringBillingVO.getLastFour()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getCardHolderName()))
            {
                sb.append(" card_holder_name='"+recurringBillingVO.getCardHolderName()+"' AND");
                countquery.append("card_holder_name='"+recurringBillingVO.getCardHolderName()+"' AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getTerminalid()))
            {
                sb.append(" terminalid IN "+recurringBillingVO.getTerminalid()+" AND");
                countquery.append("terminalid IN "+recurringBillingVO.getTerminalid()+" AND ");
            }
            sb.append(" memberid="+recurringBillingVO.getMemberId()+" ORDER BY recurring_subscription_id desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            countquery.append("memberid=").append(recurringBillingVO.getMemberId());

            /*if(functions.isValueNull(recurringBillingVO.getOriginTrackingId()) && !functions.isValueNull(recurringBillingVO.getRbid()))
            {
                sb.append(" where originatingTrackingid="+recurringBillingVO.getOriginTrackingId()+" AND memberid="+recurringBillingVO.getMemberId());
                //countquery.append("originatingTrackingid="+trackingid+" AND memberid="+memberid);
            }
            if(!functions.isValueNull(recurringBillingVO.getOriginTrackingId()) && functions.isValueNull(recurringBillingVO.getRbid()))
            {
                sb.append(" where rbid="+recurringBillingVO.getRbid()+" AND memberid="+recurringBillingVO.getMemberId());
                //countquery.append("rbid="+rbid+" AND memberid="+memberid);
            }
            else
            {
                sb.append(" where memberid="+recurringBillingVO.getMemberId()+" ORDER BY rbid desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());
                //countquery.append("memberid="+memberid+" ORDER BY rbid desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());
                countquery.append("memberid="+recurringBillingVO.getMemberId());
            }*/
            //sb.append("ORDER BY rbid desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());

            p = conn.prepareStatement(sb.toString());
            log.debug("recurring---"+p);
            rs = p.executeQuery();

            while(rs.next())
            {
                RecurringBillingVO recurringBillingVO1 = new RecurringBillingVO();
                recurringBillingVO1.setRecurring_subscrition_id(rs.getString("recurring_subscription_id"));
                recurringBillingVO1.setOriginTrackingId(rs.getString("originatingTrackingid"));
                recurringBillingVO1.setInterval(rs.getString("rec_interval"));
                recurringBillingVO1.setFrequency(rs.getString("rec_frequency"));
                recurringBillingVO1.setRunDate(rs.getString("rec_runDate"));
                recurringBillingVO1.setRecurringStatus(rs.getString("recurring_status"));
                recurringBillingVO1.setRbid(rs.getString("rbid"));
                recurringBillingVO1.setAmount(rs.getString("amount"));
                recurringBillingVO1.setFirstSix(rs.getString("first_six"));
                recurringBillingVO1.setLastFour(rs.getString("last_four"));
                recurringBillingVO1.setCardHolderName(rs.getString("card_holder_name"));
                recurringBillingVO1.setRecurringRegisterDate(rs.getString("timestamp"));
                recurringBillingVO1.setRecurringType(rs.getString("recurringType"));
                recurringBillingVO1.setTerminalid(rs.getString("terminalid"));
                listOfVo.add(recurringBillingVO1);
            }
            ResultSet rs2 = p.executeQuery(countquery.toString());
            if(rs2.next())
            {
                paginationVO.setTotalRecords(rs2.getInt(1));
            }
            log.debug("rb subc query---"+sb.toString()+"size---"+listOfVo.size()+"---"+rs2.getInt(1));
            log.debug("countquery---"+p);
        }
        catch (SystemError se)
        {
            log.error("SystemError===",se);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQLException===",e);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }
        return listOfVo;
    }

    public void updateRecurringSubscriptionDetails(RecurringBillingVO recurringBillingVO,String rbid)throws PZDBViolationException
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String updateQuery = "UPDATE recurring_transaction_subscription SET rec_interval=?,rec_frequency=?,rec_runDate=?,amount=? WHERE rbid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1,recurringBillingVO.getInterval());
            preparedStatement.setString(2,recurringBillingVO.getFrequency());
            preparedStatement.setString(3,recurringBillingVO.getRunDate());
            preparedStatement.setString(4,recurringBillingVO.getAmount());
            preparedStatement.setString(5,rbid);
            preparedStatement.executeUpdate();

            log.debug("updateRecurringSubscriptionDetails query---"+updateQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","updateRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","updateRecurringSubscriptionDetails()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
    }

    public void deleteRecurringSubscription(String rbid)throws PZDBViolationException
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String deleteQuery = "DELETE FROM recurring_transaction_subscription WHERE rbid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1,rbid);
            preparedStatement.executeUpdate();

            log.debug("deleteRecurringSubscription query---"+deleteQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","deleteRecurringSubscription()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","deleteRecurringSubscription()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
    }

    public void updateActivateDeactivateStatus(RecurringBillingVO recurringBillingVO,String rbid)throws PZDBViolationException
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String statusQuery = "UPDATE recurring_transaction_subscription SET recurring_status=? WHERE rbid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(statusQuery);
            preparedStatement.setString(1,recurringBillingVO.getActiveDeactive());
            preparedStatement.setString(2,rbid);

            preparedStatement.executeUpdate();
            log.debug("updateActivateDeactivateStatus query---"+statusQuery);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","updateActivateDeactivateStatus()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","updateActivateDeactivateStatus()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
    }

    public void updateRbidForSuccessfullRebill(String rbid,String first_six,String last_four, String trackingID) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {

                connection=Database.getConnection();
                String query="update recurring_transaction_subscription set first_six=?,last_four=?,rbid=? where originatingTrackingid=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,first_six);
                preparedStatement.setString(2,last_four);
                preparedStatement.setString(3,rbid);
                preparedStatement.setString(4,trackingID);

                preparedStatement.executeUpdate();
                log.debug("getRbidForRebill query---"+preparedStatement);

        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRbidForRebill()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch(SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRbidForRebill()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void updateSubscriptionAfterBankCall(String rbId, String first_six, String last_four, String trackingId, String bankTransactionId) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "update recurring_transaction_subscription set first_six=?,last_four=?,rbid=?,original_banktransaction_id=? where originatingTrackingid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, first_six);
            preparedStatement.setString(2, last_four);
            preparedStatement.setString(3, rbId);
            preparedStatement.setString(4, bankTransactionId);
            preparedStatement.setString(5, trackingId);
            preparedStatement.executeUpdate();
        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java", "updateSubscriptionAfterBankCall()", null, "Common", "Error in connection:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java", "updateSubscriptionAfterBankCall()", null, "Common", "Error in Query:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }
    public void insertEntryForPFSManualRebill(String trackingId,String amount,String name,String first_six,String last_four,String rbid) throws PZDBViolationException
    {
        int recurringId = 0;
        Connection connection = null;
        try
        {
            connection=Database.getConnection();
            String query="INSERT INTO recurring_transaction_subscription (originatingTrackingid,amount,card_holder_name,first_six,last_four,rbid,dtstamp) VALUES (?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()))";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,amount);
            ps.setString(3,name);
            ps.setString(4,first_six);
            ps.setString(5,last_four);
            ps.setString(6,rbid);

                int num = ps.executeUpdate();
                if (num == 1)
                {
                    ResultSet rs = ps.getGeneratedKeys();

                    if(rs!=null)
                    {
                        while(rs.next())
                        {
                            recurringId = rs.getInt(1);
                        }
                    }
                }
            log.debug("recurring_subscription_id===="+recurringId);
            log.debug("insert query insertRecurringSubscriptionDetails---"+ps);
        }
        catch(SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","insertEntryForPFSManualRebill()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","insertEntryForPFSManualRebill()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void deleteEntryForPFSRebill(String originatingTrackingId) throws PZDBViolationException
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE FROM recurring_transaction_subscription WHERE originatingTrackingid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,originatingTrackingId);
            preparedStatement.executeUpdate();

            log.debug("deleteEntryForPFSRebill query---"+preparedStatement);
        }
        catch (SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","deleteEntryForPFSRebill()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","deleteEntryForPFSRebill()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public Boolean isRecurringTrackingIdMatchesWithMerchantToken(String token,String originatingTrackingid,String toid) throws PZDBViolationException
    {
        Connection conn = null;
        boolean status = false;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT rm.tracking_id,r.originatingTrackingid FROM recurring_transaction_subscription AS r JOIN registration_member_mapping AS rm ON r.memberid = rm.toid \n" +
                                                    "JOIN registration_master AS tr ON tr.registration_id = rm.registration_tokenid AND registration_token=? AND r.originatingTrackingid=? AND rm.toid=?");
            PreparedStatement p = conn.prepareStatement(query.toString());
            p.setString(1,token);
            p.setString(2,originatingTrackingid);
            p.setString(3,toid);
            ResultSet rs = p.executeQuery();
            log.debug("token query----"+p);
            if(rs.next())
            {
                if(rs.getInt("tracking_id") != rs.getInt("originatingTrackingid"))
                {
                    status = true;
                }
            }
            log.debug("status----"+status);
        }
        catch (SystemError se)
        {
            status = false;
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            status = false;
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }

    public Boolean isRecurringTrackingIdMatchesPartnerWithToken(String token,String originatingTrackingid,String partnerId) throws PZDBViolationException
    {
        Connection conn = null;
        boolean status = false;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT rm.tracking_id, r.originatingTrackingid FROM registration_master AS reg, recurring_transaction_subscription AS r, registration_member_mapping AS rm WHERE r.cardtypeid = reg.cardtypeid AND reg.registration_id = rm.registration_tokenid AND reg.registration_token=? AND r.originatingTrackingid=? AND reg.partnerid=?");
            PreparedStatement p = conn.prepareStatement(query.toString());
            p.setString(1,token);
            p.setString(2,originatingTrackingid);
            p.setString(3,partnerId);
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(rs.getInt("tracking_id") != rs.getInt("originatingTrackingid"))
                {
                    status = true;
                }
            }
        }
        catch (SystemError se)
        {
            status = false;
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            status = false;
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }

    public List<RecurringBillingVO> getRecurringSubscriptionDetailsPartner(RecurringBillingVO recurringBillingVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement p=null;
        ResultSet rs=null;
        int records=15;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index


        StringBuilder sb = new StringBuilder();
        List<RecurringBillingVO> listOfVo=new ArrayList<RecurringBillingVO>();

        try
        {
            start = (pageno - 1) * records;
            end = records;

            conn = Database.getConnection();
            sb.append("select * from recurring_transaction_subscription as rts join members as m on rts.memberid=m.memberid where ");
            StringBuilder countquery = new StringBuilder("select count(*) from recurring_transaction_subscription as rts join members as m on rts.memberid=m.memberid where ");

           /* if(functions.isValueNull(recurringBillingVO.getPartnerId()))
            {
                sb.append(" partnerId="+recurringBillingVO.getPartnerId() +" AND");
                countquery.append("partnerId="+recurringBillingVO.getPartnerId()+" AND ");
            }*/
            if(functions.isValueNull(recurringBillingVO.getMemberId()))
            {
                sb.append(" rts.memberid="+recurringBillingVO.getMemberId() +" AND");
                countquery.append("rts.memberid="+recurringBillingVO.getMemberId()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getOriginTrackingId()))
            {
                sb.append(" originatingTrackingid="+recurringBillingVO.getOriginTrackingId() +" AND");
                countquery.append("originatingTrackingid="+recurringBillingVO.getOriginTrackingId()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getRecurring_subscrition_id()))
            {
                sb.append(" recurring_subscription_id="+recurringBillingVO.getRecurring_subscrition_id()+" AND");
                countquery.append("recurring_subscription_id="+recurringBillingVO.getRecurring_subscrition_id()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getFirstSix()))
            {
                sb.append(" first_six="+recurringBillingVO.getFirstSix()+" AND");
                countquery.append("first_six="+recurringBillingVO.getFirstSix()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getLastFour()))
            {
                sb.append(" last_four="+recurringBillingVO.getLastFour()+" AND");
                countquery.append("last_four="+recurringBillingVO.getLastFour()+" AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getCardHolderName()))
            {
                sb.append(" card_holder_name='"+recurringBillingVO.getCardHolderName()+"' AND");
                countquery.append("card_holder_name='"+recurringBillingVO.getCardHolderName()+"' AND ");
            }
            if(functions.isValueNull(recurringBillingVO.getTerminalid()))
            {
                sb.append(" terminalid IN "+recurringBillingVO.getTerminalid()+" AND");
                countquery.append("terminalid IN "+recurringBillingVO.getTerminalid()+" AND ");
            }
            sb.append(" partnerId="+recurringBillingVO.getPartnerId()+" ORDER BY recurring_subscription_id desc limit "+paginationVO.getStart()+","+paginationVO.getEnd());
            countquery.append("partnerId=").append(recurringBillingVO.getPartnerId());

            p = conn.prepareStatement(sb.toString());
            log.debug("recurring---"+p);
            rs = p.executeQuery();

            while(rs.next())
            {
                RecurringBillingVO recurringBillingVO1 = new RecurringBillingVO();
                recurringBillingVO1.setRecurring_subscrition_id(rs.getString("recurring_subscription_id"));
                recurringBillingVO1.setOriginTrackingId(rs.getString("originatingTrackingid"));
                recurringBillingVO1.setInterval(rs.getString("rec_interval"));
                recurringBillingVO1.setFrequency(rs.getString("rec_frequency"));
                recurringBillingVO1.setRunDate(rs.getString("rec_runDate"));
                recurringBillingVO1.setRecurringStatus(rs.getString("recurring_status"));
                recurringBillingVO1.setRbid(rs.getString("rbid"));
                recurringBillingVO1.setAmount(rs.getString("amount"));
                recurringBillingVO1.setFirstSix(rs.getString("first_six"));
                recurringBillingVO1.setLastFour(rs.getString("last_four"));
                recurringBillingVO1.setCardHolderName(rs.getString("card_holder_name"));
                recurringBillingVO1.setRecurringRegisterDate(rs.getString("timestamp"));
                recurringBillingVO1.setRecurringType(rs.getString("recurringType"));
                recurringBillingVO1.setTerminalid(rs.getString("terminalid"));
                recurringBillingVO1.setPartnerId(rs.getString("partnerId"));
                recurringBillingVO1.setMemberId(rs.getString("memberid"));
                listOfVo.add(recurringBillingVO1);
            }
            ResultSet rs2 = p.executeQuery(countquery.toString());
            if(rs2.next())
            {
                paginationVO.setTotalRecords(rs2.getInt(1));
            }
            log.debug("rb subcription query---"+sb.toString()+"size---"+listOfVo.size()+"---"+rs2.getInt(1));
            log.debug("countquery---"+p);
        }
        catch (SystemError se)
        {
            log.error("SystemError===",se);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in connection:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQLException===",e);
            PZExceptionHandler.raiseDBViolationException("RecurringDAO.java","getRecurringSubscriptionDetails()",null,"Common","Error in Query:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }
        return listOfVo;
    }
}
