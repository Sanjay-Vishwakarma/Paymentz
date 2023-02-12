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
import com.payment.verve.VervePaymentGateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Admin on 2/1/2021.
 */
public class VervepayAuthStartedAutomaticCron
{


    private static Logger logger = new Logger(VervepayAuthStartedAutomaticCron.class.getName());
    private static VervePayGatewayLogger transactionLogger = new VervePayGatewayLogger(VervepayAuthStartedAutomaticCron.class.getName());
    // private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.SafexpayCron");
    Functions functions=new Functions();
    public String vervepayAuthStartedAutomaticCron (Hashtable hashtable)
    {
        logger.error("::: Inside VervepayAuthStartedAutomaticCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Functions functions=new Functions();

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
            //AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 HOUR)>=FROM_UNIXTIME(dtstamp)
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='Flutter' AND toid in ("+memberIdforCron+") AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0";
            String wCond = "FROM transaction_common as t WHERE t.STATUS in  ('authstarted')  AND fromtype='vervepay' AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -13 HOUR)<=FROM_UNIXTIME(dtstamp) AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 HOUR)>=FROM_UNIXTIME(dtstamp)";
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted')  AND fromtype='rubixpay' and from_unixtime(dtstamp)>'2020-06-08 00:00:00' and from_unixtime(dtstamp)<'2020-06-08 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
//SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency FROM transaction_common AS t WHERE t.STATUS IN ( 'authfailed' )  AND fromtype='safexpay' AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 DAY)>=FROM_UNIXTIME(dtstamp) AND CURRENT_TIMESTAMP<=FROM_UNIXTIME(dtstamp);
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid,t.customerId,t.emailaddr,t.customerIp, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery VervepayAuthStartedAutomaticCron-----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authstartedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();



            transactionLogger.error("Select Query vervepay---" + ps+ "authStartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                String transStatus = "";
                String paymentid = "";
                String remark = "";
                String responseAmount = "";
                String RESPONSE_CODE = "";
                String STATUS = "";
                String txtStatus = "";
                String custId = "";
                String email = "";
                String ipAddress = "";

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

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("vervepayAuthStartedAutomaticCron");

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

                    entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, requestVO, auditTrailVO, null);


                transactionLogger.error("Select Query vervepay VervepayAuthStartedAutomaticCron-------" + trackingid);

                // SkrillPaymentGateway skrillPaymentGateway = new SkrillPaymentGateway(accountid);
                VervePaymentGateway vervePaymentGateway=new VervePaymentGateway(accountid);
                //  entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                // entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, requestVO, auditTrailVO, null);
                // responseVO = (CommResponseVO)skrillPaymentGateway.processInquiry(trackingid, requestVO);
                responseVO= (CommResponseVO) vervePaymentGateway.processInquiry(requestVO);
                responseAmount=responseVO.getAmount();
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();

                Double compRsAmount= Double.valueOf(responseAmount);
                Double compDbAmount= Double.valueOf(amount);

                transactionLogger.error("response amount --->"+compRsAmount);
                transactionLogger.error(" DB Amount--->"+compDbAmount);
                if(compDbAmount.equals(compRsAmount)){
                    txtStatus= responseVO.getStatus();
                    remark=responseVO.getRemark();
                    amount=responseAmount;
                }
                else{
                    remark="Failed-IRA";
                    transactionLogger.error("inside else Amount incorrect--->"+responseAmount);
                    txtStatus= "failed";
                    RESPONSE_CODE="11111";
                    amount=responseAmount;
                    responseVO.setRemark(remark);
                    responseVO.setDescription(txtStatus);
                    blacklistVO.setVpaAddress(custId);
                    blacklistVO.setIpAddress(ipAddress);
                    blacklistVO.setEmailAddress(email);
                    blacklistVO.setActionExecutorId(toid);
                    blacklistVO.setActionExecutorName("VervepayAuthStartedAutomaticCron");
                    blacklistVO.setRemark("IncorrectAmount");
                    blacklistManager.commonBlackListing(blacklistVO);
                }
                boolean isStatusChnged = false;
                if(responseVO!=null)
                {
                    if(("success").equalsIgnoreCase(txtStatus))
                    {
                        transactionLogger.error("inside success------->" + responseVO.getTransactionId());
                        transactionLogger.error("inside success------->" + responseVO.getDescription());
                        transactionLogger.error("inside success------->" + responseVO.getRemark());
                        transactionLogger.error("inside success------->" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                        //Auth Success
                        transStatus = "capturesuccess";
                        paymentid = responseVO.getTransactionId();
                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid,trackingid,amount );
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
                        captureSuccesCounter++;
                        isStatusChnged = true;
                    }
                    else if(("fail").equalsIgnoreCase(txtStatus)||("failed").equalsIgnoreCase(txtStatus))
                    {
                        transactionLogger.error("inside failed-------" + responseVO.getTransactionId());
                        transactionLogger.error("inside failed-------" + responseVO.getDescription());
                        transactionLogger.error("inside failed-------" + responseVO.getRemark());
                        transactionLogger.error("inside failed-------" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                        //Auth Success
                        transStatus = "authfailed";
                        paymentid = responseVO.getTransactionId();
                      //  updateMainTableEntry(String transStatus, String remark, String trackingid, String paymentid)
                        updateMainTableEntry(transStatus, responseVO.getRemark(), trackingid,"" ,responseAmount, RESPONSE_CODE);
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);
                        authFailedCounter++;
                        isStatusChnged = true;

                    }

                    transactionLogger.error("isStatusChnged-->"+isStatusChnged);

                }

                //update status flags


                //Sending Notification on NotificationURL
                logger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid+":::::"+transStatus);
                if(functions.isValueNull(notificationUrl)&&("capturesuccess".equalsIgnoreCase(transStatus)||"authfailed".equalsIgnoreCase(transStatus))&&isStatusChnged)
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    logger.error("inside vervePayAuthStartedAutomaticCron sending notification---"+notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingid,transStatus,responseVO.getRemark());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), transStatus, remark, GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());

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
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(SafexpayAuthStartedAutomaticCron.class.getName(), "vervepayAuthStartedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "vervepayAuthStartedAutomaticCron");
            notProcessedCounter++;

        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "vervepayAuthStartedAutomaticCron");
            notProcessedCounter++;

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(VervepayAuthStartedAutomaticCron.class.getName(), "vervepayAuthStartedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "vervepayAuthStartedAutomaticCron");
            notProcessedCounter++;


        }
        catch(PZGenericConstraintViolationException ge){
            logger.error("PZGenericConstraintViolationException",ge);
            notProcessedCounter++;

        }
        catch (Exception e){
            logger.error("Exception--->",e);
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

            transactionLogger.error("total transStatus---"+transStatus);
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,paymentid=?,captureamount=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentId);
            ps2.setString(4, amount);
            ps2.setString(5, trackingid);
            transactionLogger.error("total main table update vervepay authstarted automatic cron---"+ps2);
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

    public void updateMainTableEntry(String transStatus, String remark, String trackingid, String paymentid , String  responseamount, String  RESPONSE_CODE)
    {
        Connection connection = null;
        try
        {
            transactionLogger.error("total transStatus---"+transStatus);
            connection = Database.getConnection();
            String captureamount ="0.00";
            if("11111".equalsIgnoreCase(RESPONSE_CODE)&&functions.isValueNull(responseamount)){
                captureamount=responseamount;
            }
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,captureamount=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, captureamount);
            ps2.setString(4, trackingid);
            transactionLogger.error(" updateQuery1---"+updateQuery1);
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
