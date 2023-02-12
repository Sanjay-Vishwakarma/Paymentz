package com.directi.pg;


import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Admin on 7/15/2020.
 */
public class BegunTransactionCron
{
    private static Logger logger = new Logger(RubixpayAuthStartedCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BegunTransactionCron.class.getName());

    public String begunTransactionCron(Hashtable hashtable)
    {
        logger.error("::: Inside BegunTransactionCron method :::");

        StringBuffer success = new StringBuffer();


        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingid = null;
        String amount = null;
        String accountid = null;
        String toid = null;
        String currency = null;
        String status = null;
        String sDescription = null;
        String timestamp=null;
        String notificationUrl = "";

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;
        int authstartedCounter = 0;
        int begunCounter = 0;
        int failedCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String FailedStatus = "failed";

        try
        {

            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('begun') AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0 ";
         // AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0
//            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('begun') and from_unixtime(dtstamp)>'2020-08-25 00:00:00' and from_unixtime(dtstamp)<'2020-08-25 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery -----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                begunCounter = rset.getInt(1);
            // Database.closeResultSet(rset);
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            transactionLogger.error("Select Query BegunTransactionCron---" + ps+ "begunCounter---"+begunCounter);

            while (resultSet.next())
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                paymentid = resultSet.getString("paymentid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");
                timestamp = resultSet.getString("timestamp");
                notificationUrl = resultSet.getString("notificationUrl");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("BegunTransactionCron");

                CommRequestVO requestVO = new CommRequestVO();
                CommResponseVO responseVO = null;
                CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO =new CommAddressDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setResponseHashInfo(paymentid);
                commTransactionDetailsVO.setResponsetime(String.valueOf(dateFormat.format(date)));

                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                requestVO.setAddressDetailsVO(commAddressDetailsVO);
                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.error("Select Query BegunTransactionCron-------" + trackingid);
                transactionLogger.error("-----inside failed-----"+status);
                    //main table update
                insertBinTableEntry(FailedStatus, trackingid);
                updateMainTableEntry(FailedStatus,trackingid);
                    //Detail Table Entry
                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                    failedCounter++;
            }


            transactionLogger.error("begunCounter------------------>"+begunCounter);
            transactionLogger.error("FailedCounter------------------>"+failedCounter);
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(BegunTransactionCron.class.getName(), "begunTransactionCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "BegunTransactionCron");
        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "BegunTransactionCron");
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(BegunTransactionCron.class.getName(), "begunTransactionCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "BegunTransactionCron");


        }
        catch(PZGenericConstraintViolationException ge){
            logger.error("PZGenericConstraintViolationException",ge);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);

            //Database.closeResultSet(resultSet);
        }

        transactionLogger.error("total count---"+success);

        return success.toString();

    }

    public void updateMainTableEntry(String transStatus, String trackingid)
    {
        transactionLogger.error("-----inside main update-----"+transStatus);

        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            logger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }
 public void insertBinTableEntry(String transStatus, String trackingid)
    {
        transactionLogger.error("-----inside bin insert-----"+transStatus);
         Functions functions = new Functions();
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String qry = "insert into bin_details (icicitransid, accountid, bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2,emailaddr,isSuccessful,isSettled,isRefund,isChargeback,isFraud,isRollingReserveKept,isRollingReserveReleased) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(qry);
            if (functions.isValueNull(trackingid))
            {
                pstmt.setString(1, trackingid);
            }
            else
            {
                pstmt.setString(1, "");
            }

                pstmt.setString(2, "0");

                pstmt.setString(3, "");

                pstmt.setString(4, "");

                pstmt.setString(5, "");

                pstmt.setString(6, "");

                pstmt.setString(7, "");

                pstmt.setString(8, "");

                pstmt.setString(9, "");

                pstmt.setString(10, "");

                pstmt.setString(11, "");
                pstmt.setString(12,"N");
                pstmt.setString(13,"N");
                pstmt.setString(14,"N");
                pstmt.setString(15,"N");
                pstmt.setString(16,"N");
                pstmt.setString(17,"N");
                pstmt.setString(18,"Y");

            transactionLogger.error("bin insert query---->"+ pstmt);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException---->", e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError---->", systemError);
        }


        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }

}



