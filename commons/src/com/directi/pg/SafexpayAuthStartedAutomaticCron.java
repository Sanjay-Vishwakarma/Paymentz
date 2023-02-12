package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
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
import com.payment.safexpay.SafexPayPaymentGateway;
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
 * Created by Admin on 12/11/2020.
 */
public class SafexpayAuthStartedAutomaticCron
{
    private static SafexPayGatewayLogger transactionLogger = new SafexPayGatewayLogger(SafexpayAuthFailedAutomaticCron.class.getName());
    Functions functions=new Functions();
   // private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.SafexpayCron");
    public String safexpayAuthStartedAutomaticCron (Hashtable hashtable)
    {

        transactionLogger.error("::: Inside SafexpayAuthStartedAutomaticCron method :::");
        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();


        Connection connection = null;
        ResultSet resultSet=null;
        PreparedStatement ps=null;

        String trackingid = null;
        String amount = null;
        String accountid = null;
        String toid = null;
        String currency = null;
        String dbstatus = null;
        String sDescription = null;
        String timestamp=null;
        String notificationUrl = "";
        String email = "";
        String ipAddress = "";
        String custId = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";
        String ccnum = "";
        String restatus = "";


        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        //int authsucessCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int pendingCounter = 0;
        int refundCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;



        //  String memberIdforCron = RB.getString("MEMBERID_FOR_CRON");
        // logger.error("Memberid for Authstarted Cron---"+memberIdforCron);

        try
        {

            connection = Database.getConnection();
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='Flutter' AND toid in ("+memberIdforCron+") AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0";
            String wCond = "FROM transaction_common as t WHERE t.STATUS in  ('authstarted')  AND fromtype='safexpay' AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -13 HOUR)<=FROM_UNIXTIME(dtstamp) AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 HOUR)>=FROM_UNIXTIME(dtstamp)";
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted')  AND fromtype='rubixpay' and from_unixtime(dtstamp)>'2020-06-08 00:00:00' and from_unixtime(dtstamp)<'2020-06-08 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
//SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency FROM transaction_common AS t WHERE t.STATUS IN ( 'authfailed' )  AND fromtype='safexpay' AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 DAY)>=FROM_UNIXTIME(dtstamp) AND CURRENT_TIMESTAMP<=FROM_UNIXTIME(dtstamp);
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.customerId,t.emailaddr,t.customerIp,t.ccnum, t.status,t.name, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency ,t.ccnum " + wCond;
            transactionLogger.error("selectQuery SafexpayAuthStartedAutomaticCron-----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();



            transactionLogger.error("Select Query safexpay---" + ps+ "authStartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                String transStatus = "";
                String paymentid = "";
                String remark = "";
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                paymentid = resultSet.getString("paymentid");
                sDescription = resultSet.getString("description");
                dbstatus = resultSet.getString("status");
                currency = resultSet.getString("currency");
                timestamp = resultSet.getString("timestamp");
                notificationUrl = resultSet.getString("notificationUrl");
                custId  = resultSet.getString("customerId");
                email  =resultSet.getString("emailaddr") ;
                ipAddress = resultSet.getString("customerIp");
                cardHolderName = resultSet.getString("name");
                ccnum = resultSet.getString("ccnum");
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("SepAuthStartedAutomaticCron");

                if(functions.isValueNull(ccnum)){
                    ccnum= PzEncryptor.decryptPAN(ccnum);
                    firstSix=functions.getFirstSix(ccnum);
                    lastFour=functions.getLastFour(ccnum);
                }
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
                transactionLogger.error("Select Query safexpay SafexpayAuthStartedAutomaticCron-------" + trackingid);

                // SkrillPaymentGateway skrillPaymentGateway = new SkrillPaymentGateway(accountid);
                SafexPayPaymentGateway safexpayPaymentGateway=new SafexPayPaymentGateway(accountid);
                //  entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                // entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, requestVO, auditTrailVO, null);
                // responseVO = (CommResponseVO)skrillPaymentGateway.processInquiry(trackingid, requestVO);
                responseVO= (CommResponseVO) safexpayPaymentGateway.processInquiry(requestVO);


                String responseamount=responseVO.getAmount();
                String TransactionStatus=responseVO.getTransactionStatus();
                String message=responseVO.getRemark();
                String RESPONSE_CODE=responseVO.getAuthCode();
                transactionLogger.error("safexpay authstarted cron common inquiry response amount --->"+responseamount);
                transactionLogger.error("safexpay authstarted cron inquiry response TransactionStatus "+trackingid+"--->"+TransactionStatus);
                transactionLogger.error("safexpay authstarted cron inquiry response remark message --->"+message);
                Double compRsAmount= Double.valueOf(responseamount);
                Double compDbAmount= Double.valueOf(amount);
                transactionLogger.error("common inquiry response amount --->"+compRsAmount);
                transactionLogger.error(" DB Amount--->"+compDbAmount);
                if(!compDbAmount.equals(compRsAmount)){
                    TransactionStatus= "failed";
                    message="Failed-IRA";
                    responseVO.setRemark(message);
                    transactionLogger.error("safexpay authstarted cron inside else Amount incorrect--->"+responseamount);
                    RESPONSE_CODE="11111";
                    blacklistVO.setVpaAddress(custId);
                    blacklistVO.setIpAddress(ipAddress);
                    blacklistVO.setEmailAddress(email);
                    blacklistVO.setActionExecutorId(toid);
                    blacklistVO.setActionExecutorName("SafexpayAuthStartedAutomaticCron");
                    blacklistVO.setRemark("IncorrectAmount");
                    blacklistVO.setFirstSix(firstSix);
                    blacklistVO.setLastFour(lastFour);
                    blacklistVO.setName(cardHolderName);
                    blacklistManager.commonBlackListing(blacklistVO);
                }

                boolean isStatusChnged = false;
                if(responseVO!=null)
                {
                    if(("success").equalsIgnoreCase(TransactionStatus))
                    {
                        transactionLogger.error("inside success------->" + responseVO.getTransactionId());
                        transactionLogger.error("inside success------->" + responseVO.getDescription());
                        transactionLogger.error("inside success------->" + responseVO.getRemark());
                        transactionLogger.error("inside success------->" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                        //Auth Success
                        restatus="success";
                        transStatus = "capturesuccess";
                        paymentid = responseVO.getTransactionId();
                        insertMainTableEntry(transStatus, message, paymentid,trackingid,amount );
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                        captureSuccesCounter++;
                        isStatusChnged = true;
                    }
                    else if(("fail").equalsIgnoreCase(TransactionStatus)|| ("failed").equalsIgnoreCase(TransactionStatus))
                    {
                        transactionLogger.error("inside failed-------" + responseVO.getTransactionId());
                        transactionLogger.error("inside failed-------" + responseVO.getDescription());
                        transactionLogger.error("inside failed-------" + responseVO.getRemark());
                        transactionLogger.error("inside failed-------" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                        //Auth Success
                        restatus="failed";
                        transStatus = "authfailed";
                        paymentid = responseVO.getTransactionId();
                        updateMainTableEntry(transStatus, message, trackingid, paymentid,responseamount, RESPONSE_CODE);
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                        authFailedCounter++;
                        isStatusChnged = true;
                    }

                    transactionLogger.error("isStatusChnged-->"+isStatusChnged);

                }

                //update status flags
                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);

                //Sending Notification on NotificationURL
                transactionLogger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid);
                if(functions.isValueNull(notificationUrl)&&("success".equalsIgnoreCase(restatus)||"failed".equalsIgnoreCase(restatus)))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    transactionLogger.error("inside SafexpayAuthStartedAutomaticCron sending notification---"+notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if(functions.isValueNull(ccnum))
                        transactionDetailsVO.setCcnum(ccnum);
                    if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingid,restatus,responseVO.getRemark());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), restatus, remark, GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());

                }
            }

            transactionLogger.error("authstartedCounter------------------>"+authstartedCounter);
            transactionLogger.error("captureSuccesCounter------------------>"+captureSuccesCounter);
            transactionLogger.error("pendingCounter------------------>"+pendingCounter);
            transactionLogger.error("authFailedCounter------------------>"+authFailedCounter);
            transactionLogger.error("FailedCounter------------------>"+failedCounter);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(SafexpayAuthStartedAutomaticCron.class.getName(), "safexpayAuthStartedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "SafexpayAuthStartedAutomaticCron");
            notProcessedCounter++;

        }

        catch (PZDBViolationException pzd)
        {
            transactionLogger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "SafexpayAuthStartedAutomaticCron");
            notProcessedCounter++;

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(SafexpayAuthStartedAutomaticCron.class.getName(), "safexpayAuthStartedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "safexpayAuthStartedAutomaticCron");
            notProcessedCounter++;


        }
        catch(PZGenericConstraintViolationException ge){
            transactionLogger.error("PZGenericConstraintViolationException",ge);
            notProcessedCounter++;

        }
        catch (Exception e){
            transactionLogger.error("Exception--->",e);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);

            //Database.closeResultSet(resultSet);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>"+authstartedCounter+"<br>");
        success.append("<br>");
        success.append("<b>Authorization Failed : </b>"+authFailedCounter+"<br>");
        success.append("<b>Capture Successful : </b>"+captureSuccesCounter+"<br>");
        success.append("<b>Refunded : </b>"+refundCounter+"<br>");
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authFailedCounter+captureSuccesCounter+refundCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table

        transactionLogger.error("total count---"+success);

      /*  AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);*/
        return success.toString();

    }

    public void insertMainTableEntry(String transStatus, String remark, String paymentId, String trackingid,String amount)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,paymentid=?,captureamount=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentId);
            ps2.setString(4, amount);
            ps2.setString(5, trackingid);
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

    public void updateMainTableEntry(String transStatus, String remark, String trackingid, String paymentid,String rsAmount, String rsCode)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 ="";
            String captureamount ="0.00";
            if("11111".equalsIgnoreCase(rsCode)&&functions.isValueNull(rsAmount)){
                captureamount=rsAmount;
            }
             updateQuery1 = "UPDATE transaction_common SET status=?,remark=?, paymentid=?,captureamount=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentid);
            ps2.setString(4, captureamount);
            ps2.setString(5, trackingid);
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
