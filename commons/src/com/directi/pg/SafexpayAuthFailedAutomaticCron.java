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
import com.payment.Rubixpay.RubixpayPaymentGateway;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 10/9/2020.
 */
public class SafexpayAuthFailedAutomaticCron
{
    private static SafexPayGatewayLogger transactionLogger = new SafexPayGatewayLogger(SafexpayAuthFailedAutomaticCron.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.SafexpayCron");


    public String safexpayAuthFailedAutomaticCronComeon10029(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10029");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
    public String safexpayAuthFailedAutomaticCronUNIVERSEGAMING10030(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10030");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
    public String safexpayAuthFailedAutomaticCronSAFECHARGE10031(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10031");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
    public String safexpayAuthFailedAutomaticCronOLYMPTRADE10035(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10035");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
    public String safexpayAuthFailedAutomaticCronIQMerchant10036(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10036");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
 public String safexpayAuthFailedAutomaticCronIXMGLOBAL10043(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10043");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
 public String safexpayAuthFailedAutomaticCronPARIMATCH10044(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10044");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
public String safexpayAuthFailedAutomaticCronEXPERTOPTION10061(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10061");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
public String safexpayAuthFailedAutomaticCronGULF10062(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10062");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
public String safexpayAuthFailedAutomaticCronCARDPAYZ10064(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10064");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }
public String safexpayAuthFailedAutomaticCronREINVENT10066(Hashtable hashtable){

        Hashtable memberHashTable= new Hashtable();
        memberHashTable.put("memberId","10066");

        return safexpayAuthFailedAutomaticCron(memberHashTable);
    }




    public String safexpayAuthFailedAutomaticCron(Hashtable hashtable)
    {
        transactionLogger.error("safexpayAuthFailedAutomaticCron  hashtable--->"+hashtable);
        String memberidXML= (String) hashtable.get("memberId");
        transactionLogger.error("safexpayAuthFailedAutomaticCron memberidXML--->"+memberidXML);
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
        String email = "";
        String ipAddress = "";
        String custId = "";
        String ccnum = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";
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


       // String queryStatus =RB.getString("status") ;

        //  String memberIdforCron = RB.getString("MEMBERID_FOR_CRON");
        // logger.error("Memberid for Authstarted Cron---"+memberIdforCron);

        try
        {

            connection = Database.getConnection();
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='Flutter' AND toid in ("+memberIdforCron+") AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0";
            String wCond = "FROM transaction_common as t WHERE t.STATUS in  ('authfailed')  AND fromtype='safexpay' AND toid in ("+memberidXML+") AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -13 HOUR)<=FROM_UNIXTIME(dtstamp) AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 HOUR)>=FROM_UNIXTIME(dtstamp)";
            // String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted')  AND fromtype='rubixpay' and from_unixtime(dtstamp)>'2020-06-08 00:00:00' and from_unixtime(dtstamp)<'2020-06-08 14:00:00' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) >0" ;
//SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency FROM transaction_common AS t WHERE t.STATUS IN ( 'authfailed' )  AND fromtype='safexpay' AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -1 DAY)>=FROM_UNIXTIME(dtstamp) AND CURRENT_TIMESTAMP<=FROM_UNIXTIME(dtstamp);
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.customerId,t.emailaddr,t.customerIp,t.ccnum, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            transactionLogger.error("selectQuery SafexpayAuthFailedAutomaticCron-----------" + selectQuery);
            String cQuery = "SELECT count(*) " + wCond;

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next())
                authFailedCounter = rset.getInt(1);
            // Database.closeResultSet(rset);

            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();



            transactionLogger.error("Select Query safexpay---" + ps+ "authFailedCounter---"+authFailedCounter);

            while (resultSet.next())
            {
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                String transStatus = "";
                String paymentid = "";
                String remark = "";
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
                ccnum = resultSet.getString("ccnum");

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("SFXAuthFailedAutomaticCron");

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
                transactionLogger.error("Select Query safexpay SafexpayAuthFailedAutomaticCron-------" + trackingid);

                // SkrillPaymentGateway skrillPaymentGateway = new SkrillPaymentGateway(accountid);
               SafexPayPaymentGateway safexpayPaymentGateway=new SafexPayPaymentGateway(accountid);
                //  entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
             // entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, requestVO, auditTrailVO, null);
                // responseVO = (CommResponseVO)skrillPaymentGateway.processInquiry(trackingid, requestVO);
                responseVO= (CommResponseVO) safexpayPaymentGateway.processInquiry(requestVO);

                String responseamount=responseVO.getAmount();
                String TransactionStatus=responseVO.getTransactionStatus();
                String message=responseVO.getRemark();
                String code=responseVO.getAuthCode();
                String RESPONSE_CODE="";
                transactionLogger.error("safexpay authFailed cron common inquiry response amount --->"+responseamount);
                transactionLogger.error("safexpay authFailed cron inquiry response TransactionStatus "+trackingid+"--->"+TransactionStatus);
                transactionLogger.error("safexpay authFailed cron inquiry response remark message --->"+message);
                transactionLogger.error("safexpay authFailed cron inquiry response response code --->"+code);
                Double compRsAmount= Double.valueOf(responseamount);
                Double compDbAmount= Double.valueOf(amount);
                transactionLogger.error("common inquiry response amount --->"+compRsAmount);
                transactionLogger.error(" DB Amount--->"+compDbAmount);
                if(!compDbAmount.equals(compRsAmount)){
                    TransactionStatus= "failed";
                    message="Failed-IRA";
                    transactionLogger.error("safexpay authFailed cron inside else Amount incorrect--->"+responseamount);
                    RESPONSE_CODE="11111";
                    blacklistVO.setVpaAddress(custId);
                    blacklistVO.setIpAddress(ipAddress);
                    blacklistVO.setEmailAddress(email);
                    blacklistVO.setActionExecutorId(toid);
                    blacklistVO.setActionExecutorName("SafexpayAuthFailedAutomaticCron");
                    blacklistVO.setRemark("IncorrectAmount");
                    blacklistVO.setFirstSix(firstSix);
                    blacklistVO.setLastFour(lastFour);
                    blacklistVO.setName(cardHolderName);
                    blacklistManager.commonBlackListing(blacklistVO);
                }

                boolean isStatusChnged = false;
                if(responseVO!=null)
                {
                    if(("success").equalsIgnoreCase(responseVO.getStatus()))
                    {
                        transactionLogger.error("inside success------->" + responseVO.getTransactionId());
                        transactionLogger.error("inside success------->" + responseVO.getDescription());
                        transactionLogger.error("inside success------->" + responseVO.getRemark());
                        transactionLogger.error("inside success------->" + responseVO.getStatus());
                        // responseVO.setRemark("Transaction Successful");

                        //Auth Success
                        transStatus = "capturesuccess";
                        restatus = "success";
                        paymentid = responseVO.getTransactionId();
                        insertMainTableEntry(transStatus, message, paymentid,trackingid,amount );
                        //Detail Table Entry
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                        captureSuccesCounter++;
                        isStatusChnged = true;
                    }

                    transactionLogger.error("isStatusChnged-->"+isStatusChnged);

                }

                //update status flags
                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus);

                //Sending Notification on NotificationURL
                transactionLogger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid);
                if(functions.isValueNull(notificationUrl)&&("capturesuccess".equalsIgnoreCase(transStatus)))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    transactionLogger.error("inside SafexpayAuthFailedAutomaticCron sending notification---"+notificationUrl);

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
            PZExceptionHandler.raiseAndHandleDBViolationException(SafexpayAuthFailedAutomaticCron.class.getName(), "safexpayAuthFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "SafexpayAuthFailedAutomaticCron");
            notProcessedCounter++;

        }

        catch (PZDBViolationException pzd)
        {
            transactionLogger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "SafexpayAuthFailedAutomaticCron");
            notProcessedCounter++;

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(SafexpayAuthFailedAutomaticCron.class.getName(), "safexpayAuthFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "safexpayAuthFailedAutomaticCron");
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

    public void updateMainTableEntry(String transStatus, String remark, String trackingid)
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
