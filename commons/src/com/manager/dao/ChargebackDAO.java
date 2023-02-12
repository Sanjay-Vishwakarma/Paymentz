package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.payoutVOs.CBReasonsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/23/14
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargebackDAO
{
    private static Logger logger= new Logger(ChargebackDAO.class.getName());
    private static Functions functions = new Functions();

    public List<TransactionVO> getQwipiChargebackList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList<TransactionVO>();
        ResultSet rsQwipiCbList= null;
        int counter =1;
        try
        {
            conn = Database.getRDBConnection();

            StringBuffer query = new StringBuffer("SELECT T.trackingid,T.toid,T.description,T.amount,STATUS,T.qwipiPaymentOrderNumber,T.accountid,BD.isRetrivalRequest FROM transaction_qwipi AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE STATUS IN('capturesuccess','settled','reversed','markedforreversal')");

            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getQwipiPaymentOrderNumber()))
            {
                query.append(" and T.qwipiPaymentOrderNumber= ?");
            }
//            if (functions.isValueNull(transactionVO.getTrackingId()))
//            {
//                query.append(" and T.trackingid= ?");
//            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";

            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psQwipiCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psQwipiCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getQwipiPaymentOrderNumber()))
            {
                psQwipiCbList.setString(counter,transactionVO.getQwipiPaymentOrderNumber());
                counter++;
            }
//            if (functions.isValueNull(transactionVO.getTrackingId()))
//            {
//                psQwipiCbList.setString(counter,transactionVO.getTrackingId());
//                counter++;
//            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psQwipiCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psQwipiCbList.setString(counter,inputDateVO.getTdtstamp());
            }

            rsQwipiCbList = psQwipiCbList.executeQuery();
            logger.debug("CBDAO query---->"+query.toString());
            while(rsQwipiCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsQwipiCbList.getString("trackingid"));
                transactionVO1.setToid(rsQwipiCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsQwipiCbList.getString("description"));
                transactionVO1.setAmount(rsQwipiCbList.getString("amount"));
                transactionVO1.setStatus(rsQwipiCbList.getString("status"));
                transactionVO1.setAccountId(rsQwipiCbList.getString("accountid"));
                transactionVO1.setQwipiPaymentOrderNumber(rsQwipiCbList.getString("qwipiPaymentOrderNumber"));
                transactionVO1.setRetrivalRequest(rsQwipiCbList.getString("isRetrivalRequest"));
                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfQwipiCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCountOfQwipiCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getQwipiPaymentOrderNumber()))
            {
                psCountOfQwipiCbList.setString(counter,transactionVO.getQwipiPaymentOrderNumber());
                counter++;
            }
//            if (functions.isValueNull(transactionVO.getTrackingId()))
//            {
//                psCountOfQwipiCbList.setString(counter,transactionVO.getTrackingId());
//                counter++;
//            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfQwipiCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfQwipiCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsQwipiCbList =psCountOfQwipiCbList.executeQuery();

            if(rsQwipiCbList.next())
            {
                paginationVO.setTotalRecords(rsQwipiCbList.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getQwipiChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null,se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getQwipiChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }

    public List<TransactionVO> getEcoreChargebackList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList<TransactionVO>();
        ResultSet rsEcoreCbList= null;
        int counter =1;
        try
        {
            conn = Database.getRDBConnection();

            StringBuffer query = new StringBuffer("select trackingid,toid,description,amount,status,ecorePaymentOrderNumber,accountid from transaction_ecore where status IN('capturesuccess','settled','reversed','markedforreversal')");

            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and toid= ?");
            }
            if (functions.isValueNull(transactionVO.getEcorePaymentOrderNumber()))
            {
                query.append(" and ecorePaymentOrderNumber= ?");
            }
//            if (functions.isValueNull(transactionVO.getTrackingId()))
//            {
//                query.append(" and trackingid= ?");
//            }
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

            PreparedStatement psEcoreCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psEcoreCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEcorePaymentOrderNumber()))
            {
                psEcoreCbList.setString(counter,transactionVO.getEcorePaymentOrderNumber());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psEcoreCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psEcoreCbList.setString(counter,inputDateVO.getTdtstamp());
            }

            rsEcoreCbList = psEcoreCbList.executeQuery();
            logger.debug("CBDAO query---->"+query.toString());
            while(rsEcoreCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();

                transactionVO1.setTrackingId(rsEcoreCbList.getString("trackingid"));
                transactionVO1.setToid(rsEcoreCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsEcoreCbList.getString("description"));
                transactionVO1.setAmount(rsEcoreCbList.getString("amount"));
                transactionVO1.setStatus(rsEcoreCbList.getString("status"));
                transactionVO1.setAccountId(rsEcoreCbList.getString("accountid"));
                transactionVO1.setEcorePaymentOrderNumber(rsEcoreCbList.getString("ecorePaymentOrderNumber"));

                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfEcoreCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCountOfEcoreCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEcorePaymentOrderNumber()))
            {
                psCountOfEcoreCbList.setString(counter,transactionVO.getEcorePaymentOrderNumber());
                counter++;
            }

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfEcoreCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfEcoreCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsEcoreCbList =psCountOfEcoreCbList.executeQuery();

            if(rsEcoreCbList.next())
            {
                paginationVO.setTotalRecords(rsEcoreCbList.getInt(1));
            }
            logger.debug("countQuery::"+countQuery);
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getEcoreChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getEcoreChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }
    public List<TransactionVO> getCommonChargebackList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        PreparedStatement psCommonCbList = null;
        PreparedStatement psCountOfCommonCbList = null;
        ResultSet rsCommonCbList= null;
        int counter =1;
        try
        {
            conn = Database.getConnection();

            StringBuilder query = new StringBuilder("SELECT T.trackingid,T.fromtype,T.accountid,T.toid,T.description,T.captureamount,T.refundamount,T.chargebackamount as chargebackedAmount,T.status,T.paymentid,T.remark,T.timestamp,T.currency,T.firstname,T.lastname,T.notificationUrl,(T.captureamount-T.refundamount-T.chargebackamount) AS chargebackamount,BD.isRetrivalRequest,CONCAT(BD.first_six,'******',BD.last_four) as cardnumber FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE STATUS IN('capturesuccess','settled','reversed','chargebackreversed','chargeback') AND T.captureamount>T.chargebackamount");

            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and T.totype= ?");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }

            StringBuilder countQuery = new StringBuilder("Select count(*) from (").append(query.toString()).append(") as temp");
            query.append("  order by trackingid desc LIMIT ").append(paginationVO.getStart()).append(",").append(paginationVO.getEnd());

            psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getToType()))
            {
                psCommonCbList.setString(counter,transactionVO.getToType());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsCommonCbList = psCommonCbList.executeQuery();
            logger.debug("query:::::"+query.toString());
            logger.debug("query:::::"+psCommonCbList.toString());
            while(rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setCapAmount(rsCommonCbList.getString("captureamount"));
                transactionVO1.setRefundAmount(rsCommonCbList.getString("refundamount"));
                transactionVO1.setChargebackAmount(rsCommonCbList.getString("chargebackedAmount"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setCardNumber(rsCommonCbList.getString("cardnumber"));
                transactionVO1.setAmount(rsCommonCbList.getString("chargebackamount"));
                transactionVO1.setRetrivalRequest(rsCommonCbList.getString("isRetrivalRequest"));
                transactionVO1.setNotificationUrl(rsCommonCbList.getString("notificationUrl"));
                cbList.add(transactionVO1);
            }
            counter=1;
            psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getToType()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToType());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            rsCommonCbList =psCountOfCommonCbList.executeQuery();

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getCommonChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getCommonChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCommonCbList);
            Database.closePreparedStatement(psCommonCbList);
            Database.closePreparedStatement(psCountOfCommonCbList);
            Database.closeConnection(conn);
        }
        return cbList;
    }
    public int getCountofCommonChargebacklistNew(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO)throws PZDBViolationException
    {
        logger.error("Entering into getCountofCommonChargebacklistNew method...");
        Connection conn=null;
        PreparedStatement psCountOfCommonCbList = null;
        PreparedStatement psCommonCbList = null;
        ResultSet rsCommonCbList= null;
        int counter=1;
        int totalRecords=0;
        try
        {
            conn= Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT T.totype,T.trackingid,T.fromtype,T.accountid,T.toid,T.description,T.captureamount,T.refundamount,T.chargebackamount as chargebackedAmount,T.notificationUrl,T.status,T.paymentid,T.remark,T.timestamp,T.currency,T.firstname,T.lastname,(T.captureamount-T.refundamount-T.chargebackamount) AS chargebackamount,BD.isRetrivalRequest,CONCAT(BD.first_six,'******',BD.last_four) as cardnumber FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE STATUS IN('capturesuccess','settled','reversed','chargebackreversed','chargeback') AND T.captureamount>T.chargebackamount");

            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and T.totype IN ("+transactionVO.getToType()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            StringBuilder countQuery = new StringBuilder("Select count(*) from (").append(query.toString()).append(") as temp");
            psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());

            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            Date date5= new Date();
            logger.error("psCountOfCommonCbList countquery starts############ "+date5.getTime());
            rsCommonCbList =psCountOfCommonCbList.executeQuery();
            logger.error("psCountOfCommonCbList countquery ends######### "+new Date().getTime());
            logger.error("psCountOfCommonCbList countquery diff######### "+(new Date().getTime()-date5.getTime()));
            logger.error("psCountOfCommonCbList countquery ######### "+psCountOfCommonCbList.toString());

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
            totalRecords= paginationVO.getTotalRecords();
        }
        catch (SystemError systemError)
        {
            logger.error("systemError::::::: ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::: ",e);
        }
        finally
        {
            Database.closeResultSet(rsCommonCbList);
            Database.closePreparedStatement(psCommonCbList);
            Database.closePreparedStatement(psCountOfCommonCbList);
            Database.closeConnection(conn);
        }
        return totalRecords;
    }
    //CREATED NEW METHOD TO GET CHARGEBACK LIST FOR PARTNER INTERFACE
    public List<TransactionVO> getCommonChargebackListNew(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO, int totalRecords) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        PreparedStatement psCommonCbList = null;
        PreparedStatement psCountOfCommonCbList = null;
        ResultSet rsCommonCbList= null;
        int counter =1;
        int start=0;
        int end=0;
        start=(paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage();
        end= paginationVO.getRecordsPerPage();

        if (totalRecords>paginationVO.getRecordsPerPage())
        {
            end= totalRecords-start;
            if (end>paginationVO.getRecordsPerPage())
            {
                end= paginationVO.getRecordsPerPage();
            }
        }
        else
        {
            end= totalRecords;
        }
        try
        {
            conn = Database.getConnection();

            StringBuilder query = new StringBuilder("SELECT T.totype,T.trackingid,T.fromtype,T.accountid,T.toid,T.description,T.captureamount,T.refundamount,T.chargebackamount as chargebackedAmount,T.notificationUrl,T.status,T.paymentid,T.remark,T.timestamp,T.currency,T.firstname,T.lastname,(T.captureamount-T.refundamount-T.chargebackamount) AS chargebackamount,BD.isRetrivalRequest,CONCAT(BD.first_six,'******',BD.last_four) as cardnumber FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE STATUS IN('capturesuccess','settled','reversed','chargebackreversed','chargeback') AND T.captureamount>T.chargebackamount");

            if (functions.isValueNull(transactionVO.getToType()))
            {
                query.append(" and T.totype IN ("+transactionVO.getToType()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }

            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }

            StringBuilder countQuery = new StringBuilder("Select count(*) from (").append(query.toString()).append(") as temp");
            query.append("  order by trackingid desc LIMIT ").append(start).append(",").append(end);
            psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderId()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
            }
            Date date4= new Date();
            logger.error("psCommonCbList query starts########### "+date4.getTime());
            rsCommonCbList = psCommonCbList.executeQuery();
            logger.error("psCommonCbList query ends####### "+new Date().getTime());
            logger.error("psCommonCbList query diff####### "+(new Date().getTime()-date4.getTime()));
            logger.error("query:::::" + query.toString());
            logger.error(" psCommonCbList query:::::" + psCommonCbList.toString());
            while(rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setToType(rsCommonCbList.getString("totype"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setCapAmount(rsCommonCbList.getString("captureamount"));
                transactionVO1.setRefundAmount(rsCommonCbList.getString("refundamount"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setCardNumber(rsCommonCbList.getString("cardnumber"));
                transactionVO1.setAmount(rsCommonCbList.getString("chargebackamount"));
                transactionVO1.setRetrivalRequest(rsCommonCbList.getString("isRetrivalRequest"));
                transactionVO1.setChargebackAmount(rsCommonCbList.getString("chargebackedAmount"));
                transactionVO1.setNotificationUrl(rsCommonCbList.getString("notificationUrl"));

                cbList.add(transactionVO1);
            }
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getCommonChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getCommonChargebackList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsCommonCbList);
            Database.closePreparedStatement(psCommonCbList);
            Database.closePreparedStatement(psCountOfCommonCbList);
            Database.closeConnection(conn);
        }
        return cbList;
    }

    public List<CBReasonsVO> getChargebackReason() throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rRs = null;
        List<CBReasonsVO> reasonList=new ArrayList();
        String reasonQuery = "select type,code,reason from cb_codes ORDER BY type ASC";
        try
        {
            conn=Database.getRDBConnection();
            rRs = Database.executeQuery(reasonQuery,conn);
            while (rRs.next())
            {
                CBReasonsVO cbReasonsVO = new CBReasonsVO();
                cbReasonsVO.setType(rRs.getString("type"));
                cbReasonsVO.setCbReasonId(rRs.getString("code"));
                cbReasonsVO.setCbreason(rRs.getString("reason"));
                reasonList.add(cbReasonsVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getChargebackReason()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getChargebackReason()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rRs);
            Database.closeConnection(conn);
        }
        return reasonList;
    }
    public List<TransactionVO> getFraudReason() throws PZDBViolationException
    {
        Connection conn = null;
        ResultSet rRs = null;
        List<TransactionVO> fraudList=new ArrayList();
        String reasonQuery = "select type,code,reason from fraud_reason";
        try
        {
            conn=Database.getRDBConnection();
            rRs = Database.executeQuery(reasonQuery,conn);
            while (rRs.next())
            {
                TransactionVO transactionVO = new TransactionVO();
                transactionVO.setType(rRs.getString("type"));
                transactionVO.setCode(rRs.getString("code"));
                transactionVO.setFraudreason(rRs.getString("reason"));
                fraudList.add(transactionVO);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getFraudReason()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getFraudReason()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rRs);
            Database.closeConnection(conn);
        }
        return fraudList;
    }
    public List<TransactionVO> getMarkFraudTransaction(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        ResultSet rsCommonCbList= null;
        int counter =1;
        String pRefund = "false";
        String status = transactionVO.getStatus();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT T.timestamp,T.trackingid,T.fromtype,T.accountid,T.paymentid,T.toid,T.description,T.emailaddr,T.firstname,T.lastname,T.name,T.status,T.amount,T.currency,BD.first_six,BD.last_four,BD.isFraud FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE BD.isFraud='N' AND STATUS IN('capturesuccess','settled','reversed','markedforreversal','authsuccessful','partialrefund','chargeback','capturestarted')");
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                query.append(" and T.timestamp= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                query.append(" and T.emailaddr= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                query.append(" and T.name= ?");
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append(" and T.status= ?");
            }*/
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and T.status= '"+status+"'");
            }

            if (functions.isValueNull(transactionVO.getAmount()))
            {
                query.append(" and T.amount= ?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                query.append(" and BD.first_six= ?");
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                query.append(" and BD.last_four= ?");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and T.status='reversed'");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            rsCommonCbList = psCommonCbList.executeQuery();
            logger.debug("query::::22:"+psCommonCbList);
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            while(rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setEmailAddr(rsCommonCbList.getString("emailaddr"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setName(rsCommonCbList.getString("name"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setAmount(rsCommonCbList.getString("amount"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setFirstSix(rsCommonCbList.getString("first_six"));
                transactionVO1.setLastFour(rsCommonCbList.getString("last_four"));
                transactionVO1.setFraudRequest(rsCommonCbList.getString("isFraud"));
                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            rsCommonCbList =psCountOfCommonCbList.executeQuery();

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }
    public List<TransactionVO> getMarkFraudTransactions(TransactionVO transactionVO, String toidList,InputDateVO inputDateVO,PaginationVO paginationVO, String partnername) throws PZDBViolationException
    {
        logger.debug("inside getMarkFraudTransaction");
        logger.debug("partnerName ++++"+partnername);
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        ResultSet rsCommonCbList= null;
        int counter =1;
        String pRefund = "false";
        String status = transactionVO.getStatus();
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT T.timestamp,T.trackingid,T.fromtype,T.accountid,T.paymentid,T.toid,T.description,T.emailaddr,T.firstname,T.lastname,T.name,T.status,T.amount,T.refundamount,T.currency,BD.first_six,BD.last_four,BD.isFraud FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE BD.isFraud='N' AND STATUS IN('capturesuccess','settled','reversed','markedforreversal','authsuccessful','partialrefund','chargeback')");
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                query.append(" and T.timestamp= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(toidList))
            {
                query.append(" and T.toid IN("+transactionVO.getToid()+")");
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                query.append(" and T.emailaddr= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                query.append(" and T.name= ?");
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append(" and T.status= ?");
            }*/
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and T.status= '"+status+"'");
            }

            if (functions.isValueNull(transactionVO.getAmount()))
            {
                query.append(" and T.amount= ?");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                query.append(" and BD.first_six= ?");
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                query.append(" and BD.last_four= ?");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and captureamount > refundamount and T.status='reversed'");
            }
            if (functions.isValueNull(partnername))
            {
                query.append(" and T.totype IN (" +partnername+ ")");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCommonCbList.setString(counter,transactionVO.getLastFour());
            }
            /*if (functions.isValueNull(partnername))
            {
                psCommonCbList.setString(counter,partnername);
            }*/

            rsCommonCbList = psCommonCbList.executeQuery();
            logger.debug("query in partner getMarkFraudTransactions fraud:::::::::"+psCommonCbList);
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            while (rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setEmailAddr(rsCommonCbList.getString("emailaddr"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setName(rsCommonCbList.getString("name"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setAmount(rsCommonCbList.getString("amount"));
                transactionVO1.setRefundAmount(rsCommonCbList.getString("refundamount"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setFirstSix(rsCommonCbList.getString("first_six"));
                transactionVO1.setLastFour(rsCommonCbList.getString("last_four"));
                transactionVO1.setFraudRequest(rsCommonCbList.getString("isFraud"));
                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getLastFour());
            }
            /*if (functions.isValueNull(partnername))
            {
                psCountOfCommonCbList.setString(counter,partnername);
            }*/

            rsCommonCbList =psCountOfCommonCbList.executeQuery();

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }
    public List<TransactionVO> getFraudAction(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        ResultSet rsCommonCbList= null;
        int counter =1;
        String status=transactionVO.getStatus();
        String pRefund = "false";
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT T.timestamp,T.trackingid,T.fromtype,T.accountid,T.paymentid,T.toid,T.description,T.emailaddr,T.firstname,T.lastname,T.name,T.status,T.amount,T.currency,BD.first_six,BD.last_four,BD.isFraud FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE BD.isFraud='Y' AND STATUS IN('capturesuccess','reversed','settled')");
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                query.append(" and T.timestamp= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid= ?");
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                query.append(" and T.emailaddr= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                query.append(" and T.name= ?");
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append(" and T.status= ?");
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                query.append(" and T.amount= ?");
            }
            logger.debug("Status::::"+status);
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and T.status= '"+status+"'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                query.append(" and BD.first_six= ?");
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                query.append(" and BD.last_four= ?");
            }

            query.append(" and captureamount > refundamount");
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and T.status='reversed'");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());

            PreparedStatement psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            rsCommonCbList = psCommonCbList.executeQuery();
            logger.debug("query::::22:"+psCommonCbList);
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            while(rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setEmailAddr(rsCommonCbList.getString("emailaddr"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setName(rsCommonCbList.getString("name"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setAmount(rsCommonCbList.getString("amount"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setFirstSix(rsCommonCbList.getString("first_six"));
                transactionVO1.setLastFour(rsCommonCbList.getString("last_four"));
                transactionVO1.setFraudRequest(rsCommonCbList.getString("isFraud"));
                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            rsCommonCbList =psCountOfCommonCbList.executeQuery();

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }

    public List<TransactionVO> getFraudActionWithPartner(TransactionVO transactionVO,String toidList,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        Connection conn = null;
        List<TransactionVO> cbList=new ArrayList();
        ResultSet rsCommonCbList= null;
        int counter =1;
        String status=transactionVO.getStatus();
        String pRefund = "false";
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query=new StringBuffer("SELECT T.timestamp,T.trackingid,T.fromtype,T.accountid,T.paymentid,T.toid,T.description,T.emailaddr,T.firstname,T.lastname,T.name,T.status,T.amount,T.refundamount,T.currency,BD.first_six,BD.last_four,BD.isFraud FROM transaction_common AS T JOIN bin_details AS BD ON T.trackingid=BD.icicitransid WHERE BD.isFraud='Y' AND STATUS IN('capturesuccess','reversed','settled')");
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                query.append(" and T.timestamp= ?");
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                query.append(" and T.dtstamp >= ?");
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                query.append(" and T.dtstamp <= ?");
            }
            if (functions.isValueNull(transactionVO.getTrackingId()))
            {
                query.append(" and trackingid IN("+transactionVO.getTrackingId()+")");
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                query.append(" and T.fromtype= ?");
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                query.append(" and T.accountid= ?");
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                query.append(" and T.paymentid= ?");
            }
            if (functions.isValueNull(transactionVO.getToid()))
            {
                query.append(" and T.toid IN("+transactionVO.getToid()+")");
            }
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                query.append(" and T.description= ?");
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                query.append(" and T.emailaddr= ?");
            }
            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                query.append(" and T.firstname= ?");
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                query.append(" and T.lastname= ?");
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                query.append(" and T.name= ?");
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                query.append(" and T.status= ?");
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                query.append(" and T.amount= ?");
            }
            if (functions.isValueNull(status))
            {
                if(status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and T.status= '"+status+"'");
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                query.append(" and T.currency= ?");
            }
            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                query.append(" and BD.first_six= ?");
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                query.append(" and BD.last_four= ?");
            }

            query.append(" and captureamount > refundamount");
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true") )
            {
                query.append(" and T.status='reversed'");
            }

            String countQuery = "Select count(*) from ("+query.toString()+") as temp";
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            logger.debug("query in :::::" + query.toString());
            logger.debug("countQuery in :::::"+countQuery.toString());

            PreparedStatement psCommonCbList= conn.prepareStatement(query.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getToid()))
            {
                psCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
           /* if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            rsCommonCbList = psCommonCbList.executeQuery();
            logger.error("query notification:::::"+psCommonCbList);
            query.append("  order by trackingid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            while(rsCommonCbList.next())
            {
                TransactionVO transactionVO1 = new TransactionVO();
                transactionVO1.setTimestamp(rsCommonCbList.getString("timestamp"));
                transactionVO1.setTrackingId(rsCommonCbList.getString("trackingid"));
                transactionVO1.setGatewayName(rsCommonCbList.getString("fromtype"));
                transactionVO1.setAccountId(rsCommonCbList.getString("accountid"));
                transactionVO1.setPaymentId(rsCommonCbList.getString("paymentid"));
                transactionVO1.setToid(rsCommonCbList.getString("toid"));
                transactionVO1.setOrderDesc(rsCommonCbList.getString("description"));
                transactionVO1.setEmailAddr(rsCommonCbList.getString("emailaddr"));
                transactionVO1.setCustFirstName(rsCommonCbList.getString("firstname"));
                transactionVO1.setCustLastName(rsCommonCbList.getString("lastname"));
                transactionVO1.setName(rsCommonCbList.getString("name"));
                transactionVO1.setStatus(rsCommonCbList.getString("status"));
                transactionVO1.setAmount(rsCommonCbList.getString("amount"));
                transactionVO1.setRefundAmount(rsCommonCbList.getString("refundamount"));
                transactionVO1.setCurrency(rsCommonCbList.getString("currency"));
                transactionVO1.setFirstSix(rsCommonCbList.getString("first_six"));
                transactionVO1.setLastFour(rsCommonCbList.getString("last_four"));
                transactionVO1.setFraudRequest(rsCommonCbList.getString("isFraud"));
                cbList.add(transactionVO1);
            }
            counter=1;

            PreparedStatement psCountOfCommonCbList= conn.prepareStatement(countQuery.toString());
            if (functions.isValueNull(transactionVO.getTimestamp()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getTimestamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getFdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getFdtstamp());
                counter++;
            }
            if (functions.isValueNull(inputDateVO.getTdtstamp()))
            {
                psCountOfCommonCbList.setString(counter,inputDateVO.getTdtstamp());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getGatewayName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getGatewayName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getAccountId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAccountId());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getPaymentId()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getPaymentId());
                counter++;
            }
            /*if (functions.isValueNull(transactionVO.getToid()) && !transactionVO.getToid().equalsIgnoreCase("0"))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getToid());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getOrderDesc()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getOrderDesc());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getEmailAddr()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getEmailAddr());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getCustFirstName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustFirstName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCustLastName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCustLastName());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getName()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getName());
                counter++;
            }
            /*if (functions.isValueNull(transactionVO.getStatus()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getStatus());
                counter++;
            }*/
            if (functions.isValueNull(transactionVO.getAmount()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getAmount());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getCurrency()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getCurrency());
                counter++;
            }

            if (functions.isValueNull(transactionVO.getFirstSix()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getFirstSix());
                counter++;
            }
            if (functions.isValueNull(transactionVO.getLastFour()))
            {
                psCountOfCommonCbList.setString(counter,transactionVO.getLastFour());
            }

            logger.debug("count query notification::::"+psCountOfCommonCbList);
            rsCommonCbList =psCountOfCommonCbList.executeQuery();

            if(rsCommonCbList.next())
            {
                paginationVO.setTotalRecords(rsCommonCbList.getInt(1));
            }
        }

        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ChargebackDAO.java", "getMarkFraudTransaction()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return cbList;
    }



}