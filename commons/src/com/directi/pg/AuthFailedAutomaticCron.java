package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
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

/**
 * Created by Admin on 2/25/2021.
 */
public class AuthFailedAutomaticCron
{

    private static Logger logger = new Logger(AuthFailedAutomaticCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AuthFailedAutomaticCron.class.getName());

    public String authFailedAutomaticCron(Hashtable hashtable)
    {
        logger.error("::: Inside AuthFailedAutomaticCron method :::");

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


        String paymentid = "";
        String FailedStatus = "authfailed";

        try
        {

            connection = Database.getConnection();
            String wCond = "FROM transaction_common AS t WHERE t.STATUS IN ('authstarted') AND t.fromtype IN('vervepay') AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -36 HOUR)>=FROM_UNIXTIME(dtstamp) ";
            // AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0
//            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('begun') and from_unixtime(dtstamp)>'2020-08-25 00:00:00' and from_unixtime(dtstamp)<'2020-08-25 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery -----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            transactionLogger.error("Select Query AuthFailedAutomaticCron---" + ps+ "Authstartedcounter---"+authstartedCounter);

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
                StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("AuthFailedAutomaticCron");
                Functions functions=new Functions();
                CommRequestVO requestVO = new CommRequestVO();

                CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO =new CommAddressDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setResponseHashInfo(paymentid);
                commTransactionDetailsVO.setResponsetime(String.valueOf(dateFormat.format(date)));

                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                requestVO.setAddressDetailsVO(commAddressDetailsVO);
                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.error("Select Query AuthFailedAutomaticCron-------" + trackingid);
                transactionLogger.error("-----dbstatus-----"+status);
                //main table update
                CommResponseVO responseVO = new CommResponseVO() ;

                responseVO.setStatus(FailedStatus);
                responseVO.setTransactionId(" ");
                responseVO.setRemark("failed");
                responseVO.setDescription("failed");
                updateMainTableEntry(FailedStatus, trackingid);
                //Detail Table Entry
                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                failedCounter++;

                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,FailedStatus);

                transactionLogger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid+":::::"+FailedStatus);
                if(functions.isValueNull(notificationUrl))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    transactionLogger.error("-----toid-----"+toid);
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                   transactionLogger.error("inside AuthFailedAutomaticCron sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingid, "Failed", responseVO.getRemark());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid),"Failed" , "failed", GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());

                }
            }


            transactionLogger.error("AuthFailedAutomaticCron authstartedCounter------------------>"+authstartedCounter);
            transactionLogger.error("AuthFailedAutomaticCron authFailedCounter------------------>"+failedCounter);
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(AuthFailedAutomaticCron.class.getName(), "authFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "AuthFailedAutomaticCron");
        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "AuthFailedAutomaticCron");
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(AuthFailedAutomaticCron.class.getName(), "authFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "AuthFailedAutomaticCron");


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

}
