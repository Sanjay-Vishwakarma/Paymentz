package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
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
 * Date: 12/19/14
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundDAO
{
    private static Logger logger= new Logger(RefundDAO.class.getName());
    private static Functions functions = new Functions();

    public List<TransactionVO> getQwipiRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {

        Connection conn = null;
        List<TransactionVO> refundList=new ArrayList<TransactionVO>();
        ResultSet rsQwipiRefundList= null;
        int counter =1;
        try
        {
            conn = Database.getConnection();

            StringBuffer query = new StringBuffer("select trackingid,toid,description,amount,status,timestamp,fromid,qwipiPaymentOrderNumber,FROM_UNIXTIME(dtstamp) as dtstamp from transaction_qwipi where status IN ('settled','capturesuccess') and trackingid>0");

            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and toid= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            String countQuery = "Select count(*) from ("+query.toString()+") as temp";

            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psQwipiRefundList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psQwipiRefundList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psQwipiRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psQwipiRefundList.setString(counter,inputDateVO.getTdtstamp());
            }

            rsQwipiRefundList=psQwipiRefundList.executeQuery();
            while(rsQwipiRefundList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsQwipiRefundList.getString("trackingid"));
                transactionVO1.setToid(rsQwipiRefundList.getString("toid"));
                transactionVO1.setOrderDesc(rsQwipiRefundList.getString("description"));
                transactionVO1.setAmount(rsQwipiRefundList.getString("amount"));
                transactionVO1.setStatus(rsQwipiRefundList.getString("status"));
                //transactionVO1.setTimestamp(rsQwipiRefundList.getString("timestamp"));
                transactionVO1.setDtStamp(rsQwipiRefundList.getString("dtstamp"));
                transactionVO1.setFromid(rsQwipiRefundList.getString("fromid"));
                transactionVO1.setQwipiPaymentOrderNumber(rsQwipiRefundList.getString("qwipiPaymentOrderNumber"));

                refundList.add(transactionVO1);
            }

            logger.debug("RefundDAO query---->"+query);


            counter=1;

            PreparedStatement psCountOfQwipiRefundList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCountOfQwipiRefundList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfQwipiRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfQwipiRefundList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsQwipiRefundList =psCountOfQwipiRefundList.executeQuery();
            if(rsQwipiRefundList.next())
            {
                paginationVO.setTotalRecords(rsQwipiRefundList.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);
        }

        catch (SystemError se)
        {
            logger.error("System Error::::::",se);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getQwipiRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Exception::::::::",e);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getQwipiRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return refundList;
    }

    public List<TransactionVO> getEcoreRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {

        Connection conn = null;
        List<TransactionVO> refundList=new ArrayList<TransactionVO>();
        ResultSet rsEcoreRefundList= null;
        int counter =1;
        try
        {
            conn = Database.getConnection();

            StringBuffer query = new StringBuffer("select trackingid,toid,description,amount,status,timestamp,fromid,ecorePaymentOrderNumber,FROM_UNIXTIME(dtstamp) as dtstamp  from transaction_ecore where status IN ('settled','capturesuccess') and trackingid>0");

            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and toid= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";

            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psEcoreRefundList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psEcoreRefundList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psEcoreRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psEcoreRefundList.setString(counter,inputDateVO.getTdtstamp());
            }

            rsEcoreRefundList = psEcoreRefundList.executeQuery();
            while(rsEcoreRefundList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsEcoreRefundList.getString("trackingid"));
                transactionVO1.setToid(rsEcoreRefundList.getString("toid"));
                transactionVO1.setOrderDesc(rsEcoreRefundList.getString("description"));
                transactionVO1.setAmount(rsEcoreRefundList.getString("amount"));
                transactionVO1.setStatus(rsEcoreRefundList.getString("status"));
                //transactionVO1.setTimestamp(rsEcoreRefundList.getString("timestamp"));
                transactionVO1.setDtStamp(rsEcoreRefundList.getString("dtstamp"));
                transactionVO1.setFromid(rsEcoreRefundList.getString("fromid"));
                transactionVO1.setEcorePaymentOrderNumber(rsEcoreRefundList.getString("ecorePaymentOrderNumber"));

                refundList.add(transactionVO1);
            }

            logger.debug("RefundDAO query---->"+query);

            counter=1;

            PreparedStatement psCountOfEcoreRefundList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCountOfEcoreRefundList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfEcoreRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfEcoreRefundList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsEcoreRefundList =psCountOfEcoreRefundList.executeQuery();

            if(rsEcoreRefundList.next())
            {
                paginationVO.setTotalRecords(rsEcoreRefundList.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);
        }

        catch (SystemError se)
        {
            logger.error("System Error::::::",se);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getEcoreRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Exception::::::::",e);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getEcoreRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return refundList;
    }

    public List<TransactionVO> getCommonRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> refundList=new ArrayList();
        ResultSet rsCommonRefundList= null;
        int counter =1;
        try
        {
            conn = Database.getConnection();

            StringBuffer query = new StringBuffer("select trackingid,accountid,toid,description,captureamount,refundamount,status,paymentid,remark,timestamp,FROM_UNIXTIME(dtstamp) as dtstamp from transaction_common where status IN ('settled','capturesuccess','reversed') and captureamount>refundamount ");

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid in("+transactionVO.getTrackingId()+")");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";

            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psCommonRefundList= conn.prepareStatement(query.toString());

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonRefundList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonRefundList.setString(counter,transactionVO.getPaymentId());
            }
            rsCommonRefundList = psCommonRefundList.executeQuery();
            while(rsCommonRefundList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsCommonRefundList.getString("trackingid"));
                transactionVO1.setAccountId(rsCommonRefundList.getString("accountid"));
                transactionVO1.setToid(rsCommonRefundList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonRefundList.getString("description"));
                transactionVO1.setAmount(rsCommonRefundList.getString("captureamount"));
                transactionVO1.setReverseAmount(rsCommonRefundList.getString("refundamount"));
                transactionVO1.setStatus(rsCommonRefundList.getString("status"));
                transactionVO1.setPaymentId(rsCommonRefundList.getString("paymentid"));
                transactionVO1.setRemark(rsCommonRefundList.getString("remark"));
                transactionVO1.setTimestamp(rsCommonRefundList.getString("timestamp"));
                transactionVO1.setDtStamp(rsCommonRefundList.getString("dtstamp"));


                refundList.add(transactionVO1);
            }

            logger.debug("RefundDAO query---->"+query);
            logger.debug("refund query for common----"+psCommonRefundList);

            counter=1;

            PreparedStatement psCountOfCommonRefundList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonRefundList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonRefundList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonRefundList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonRefundList.setString(counter,transactionVO.getPaymentId());
            }

            rsCommonRefundList =psCountOfCommonRefundList.executeQuery();

            if(rsCommonRefundList.next())
            {
                paginationVO.setTotalRecords(rsCommonRefundList.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);
        }

        catch (SystemError se)
        {
            logger.error("System Error::::::",se);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getCommonRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Exception::::::::",e);
            PZExceptionHandler.raiseDBViolationException("RefundDAO.java", "getCommonRefundList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return refundList;
    }
}
