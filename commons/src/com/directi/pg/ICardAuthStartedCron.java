package com.directi.pg;

import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 6/4/2020.
 */
public class ICardAuthStartedCron
{
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.icard");
    private ICardLogger transactionLogger=new ICardLogger(ICardAuthStartedCron.class.getName());
    public String icardAuthStartedCron(Hashtable hashtable)
    {
        transactionLogger.error("::: Inside icardAuthStartedCron method :::");

        String success = "success";
        Functions functions=new Functions();

        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingid = null;
        String amount = null;
        String accountid = null;
        String toid = null;

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        String transStatus = "";
        String paymentid = "";
        String remark = "";
        String fromDate="";
        String toDate="";
        String startDate="";
        String endDate="";
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(hashtable != null)
        {
            fromDate= (String) RB.getString("START_DATE");
            toDate= (String) RB.getString("END_DATE");
        }
        try
        {

            /*fdatetime=String.valueOf(dateFormat.parse(fromDate).getTime());
            tdatetime=String.valueOf(dateFormat.parse(toDate).getTime());*/
            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='icard' AND FROM_UNIXTIME(dtstamp) BETWEEN '"+fromDate+"' AND '"+toDate+"'";

            String selectQuery = "SELECT t.trackingid, t.toid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.debug("selectQuery -----------"+selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();



            transactionLogger.error("Select Query ICard---" + ps+ " authstartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("ICardAuthStartedCron");

                CommResponseVO responseVO = new CommResponseVO();

                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.debug("Select Query ICard-------"+trackingid);

                    //Not found transaction
                transStatus = "authfailed";
                responseVO.setRemark("Transaction Failed");
                responseVO.setDescription("Transaction Failed");
                //Detail Table Entry
                insertMainTableEntry(transStatus, responseVO.getRemark(), trackingid);
                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);

            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(ICardAuthStartedCron.class.getName(), "icardAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "ICardAuthStartedCron");
        }

        catch (PZDBViolationException pzd)
        {
            transactionLogger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "ICardAuthStartedCron");
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(ICardAuthStartedCron.class.getName(), "icardAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "ICardAuthStartedCron");

        }
        catch(PZGenericConstraintViolationException ge){
            transactionLogger.error("PZGenericConstraintViolationException",ge);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);

            //Database.closeResultSet(resultSet);
        }
        return success.toString();

    }
    public void insertMainTableEntry(String transStatus, String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, trackingid);
            transactionLogger.error("ICardCron update query-->"+ps2);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }
}
