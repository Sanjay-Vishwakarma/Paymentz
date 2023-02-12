package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.borgun.core.BorgunErrorCodeClass;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.borgun.core.BorgunResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
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
 * Created by Nikita on 8/8/2016.
 */
public class BorgunAuthStartedCron
{
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.KotakServlet");
    private static Logger logger = new Logger(BorgunAuthStartedCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BorgunAuthStartedCron.class.getName());

    public static void main(String args[]) throws SystemError
    {
        BorgunAuthStartedCron borgunAuthStartedCron = new BorgunAuthStartedCron();
        String sResponse = borgunAuthStartedCron.borgunAuthStartedCron(new Hashtable());
    }

    public String borgunAuthStartedCron(Hashtable hashtable)
    {
        logger.debug("Inside BorgunAuthStartedCron method :::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Borgun</b>" + "</u><br>");
        success.append("<br>");
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

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

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        int authsucessCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int refundCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String remark = "";

        try
        {
            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='borgun' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 12 ";

            String selectQuery = "SELECT t.trackingid, t.toid, t.status, t.amount, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency "+wCond;

            String cQuery = "SELECT count(*) "+wCond;
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            ResultSet rs = Database.executeQuery(cQuery.toString(), connection);
            rs.next();
            authstartedCounter = rs.getInt(1);

            transactionLogger.debug("Select Query Borgun---"+ps);
            while (resultSet.next())
            {
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");


                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("BorgunAuthStartedCron");

                CommRequestVO requestVO = new CommRequestVO();
                BorgunResponseVO responseVO = null;
                CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.debug("Select Query Borgun---"+trackingid);
                BorgunPaymentGateway borgunPaymentGateway = new BorgunPaymentGateway(accountid);

                transactionLogger.debug("Before inquiry---");
                responseVO = (BorgunResponseVO) borgunPaymentGateway.processInquiry(requestVO);
                transactionLogger.debug("After inquiry---"+responseVO.getErrorCode());

                transactionLogger.debug("inside in---"+responseVO.getStatus());
                if(responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    transactionLogger.debug("inside 1st if---size"+responseVO.getListMap());
                    if(responseVO.getListMap()!=null)
                    {
                        transactionLogger.debug("inside 2nd if---size");
                        if(responseVO.getListMap().get("auth")!=null)
                        {
                            responseVO.setTransactionId(paymentid);
                            responseVO.setErrorCode(responseVO.getListMap().get("auth").getActionCode());

                            responseVO.setResponseTime(responseVO.getListMap().get("auth").getTransactionDate());
                            responseVO.setTransactionType("auth");
                            if(responseVO.getListMap().get("auth").getActionCode().equalsIgnoreCase("000"))
                            {
                                transStatus = "authsuccessful";
                                paymentid = responseVO.getListMap().get("auth").getAuthorizationCode();
                                remark = "Transaction Successful";
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, responseVO, auditTrailVO, null);
                                authsucessCounter++;
                            }
                            else
                            {
                                transStatus = "authfailed";
                                paymentid = responseVO.getListMap().get("auth").getAuthorizationCode();
                                remark = BorgunErrorCodeClass.getErrorStringFromCode(responseVO.getListMap().get("auth").getActionCode());
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                                authFailedCounter++;
                            }
                        }
                        if(responseVO.getListMap().get("sale")!=null)
                        {
                            responseVO.setTransactionId(paymentid);
                            responseVO.setErrorCode(responseVO.getListMap().get("sale").getActionCode());

                            responseVO.setResponseTime(responseVO.getListMap().get("sale").getTransactionDate());
                            responseVO.setTransactionType("sale");
                            if(responseVO.getListMap().get("sale").getActionCode().equalsIgnoreCase("000"))
                            {
                                transStatus = "capturesuccess";
                                paymentid = responseVO.getListMap().get("sale").getAuthorizationCode();
                                remark = "Transaction Successful";
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                if(responseVO.getListMap().get("refund")==null)
                                {
                                    captureSuccesCounter++;
                                }
                            }
                            else
                            {
                                transStatus = "authfailed";
                                paymentid = responseVO.getListMap().get("sale").getAuthorizationCode();
                                remark = BorgunErrorCodeClass.getErrorStringFromCode(responseVO.getListMap().get("sale").getActionCode());
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                                authFailedCounter++;
                            }
                        }
                        if(responseVO.getListMap().get("refund")!=null)
                        {
                            responseVO.setTransactionId(paymentid);
                            responseVO.setErrorCode(responseVO.getListMap().get("refund").getActionCode());
                            responseVO.setResponseTime(responseVO.getListMap().get("refund").getTransactionDate());
                            responseVO.setTransactionType("refund");
                            if(responseVO.getListMap().get("refund").getActionCode().equalsIgnoreCase("000"))
                            {
                                transStatus = "reversed";
                                paymentid = responseVO.getListMap().get("refund").getAuthorizationCode();
                                remark = BorgunErrorCodeClass.getErrorStringFromCode(responseVO.getListMap().get("refund").getActionCode());
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, responseVO, auditTrailVO, null);
                                refundCounter++;
                            }
                        }
                        //main table entry
                        insertMainTableEntry(transStatus,remark,paymentid,trackingid);

                        //update status flags
                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus, connection);
                    }
                    else   //Transaction not found.
                    {
                        logger.debug("Else status is authstarted to 1---");

                        responseVO.setRemark("Transaction not found");
                        responseVO.setDescription("Transaction not found");
                        responseVO.setStatus("fail");

                        insertMainTableEntry("failed","Transaction not found using Cron","",trackingid);

                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "failed", connection);

                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);

                        failedCounter++;

                        yTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,"failed"));
                    }
                }
                else   //Transaction not found.
                {
                    transactionLogger.debug("Else status is authstarted to failed---" + responseVO.getStatus());

                    notProcessedCounter++;

                    nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
                }

            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunAuthStartedCron.class.getName(), "borgunAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Borgun Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (PZTechnicalViolationException pzt)
        {
            logger.error("PZTechnicalViolationException exception",pzt);
            PZExceptionHandler.handleTechicalCVEException(pzt, toid, "BorgunAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "BorgunAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunAuthStartedCron.class.getName(),"borgunAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Borgun Auth started cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));

        }
        finally
        {
            Database.closeConnection(connection);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");
        success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>"+authstartedCounter+"<br>");
        success.append("<br>");
        success.append("<b>Authorization Successful : </b>"+authsucessCounter+"<br>");
        success.append("<b>Authorization Failed : </b>"+authFailedCounter+"<br>");
        success.append("<b>Capture Successful : </b>"+captureSuccesCounter+"<br>");
        success.append("<b>Refunded : </b>"+refundCounter+"<br>");
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authsucessCounter+authFailedCounter+captureSuccesCounter+refundCounter+failedCounter+notProcessedCounter;
        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table
        if(notProcessedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transactions in AuthStarted / CaptureStarted</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(nTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }
        //Failed Transactions Table
        if(failedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transaction status changed from AuthStarted/Capture Started to Failed(Orders not found)</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(yTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }
        success.append(sHeader);

        transactionLogger.debug("total count---"+success);
        transactionLogger.debug("before sent mail---");
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);
        transactionLogger.debug("after sent mail---");
        return success.toString();
    }

    public void insertMainTableEntry(String transStatus, String remark, String paymentId, String trackingid)
    {
        Connection connection = null;

        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET status=?,remark=?,paymentid=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transStatus);
            ps2.setString(2, remark);
            ps2.setString(3, paymentId);
            ps2.setString(4, trackingid);
            logger.debug("Update query Borgun---"+ps2);
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
            Database.closeConnection(connection);
        }
    }


    public StringBuffer getTableHeader()
    {
        StringBuffer sHeader = new StringBuffer();

        sHeader.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">AccountID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MerchantID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">OrderID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b>");
        sHeader.append("</TD>");


        sHeader.append("</TR>");

        return sHeader;
    }

    public StringBuffer setTableData(String trackingid, String accountid, String toid, String sDescription, String currency, String captureAmount, String status)
    {
        StringBuffer nTransaction = new StringBuffer();

        nTransaction.append("<TR>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+trackingid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+accountid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+toid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+sDescription+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+currency+" "+captureAmount+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+status+"</p>");
        nTransaction.append("</TD>");

        nTransaction.append("</TR>");

        return nTransaction;
    }
    /*
    `gateway_accounts_kotak`

    SELECT * FROM transaction_common WHERE fromtype='Borgun' AND STATUS='capturesuccess'

    SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC


    SELECT trackingid,STATUS,paymentid FROM transaction_common WHERE STATUS='reversed' AND fromtype='Borgun'

    UPDATE transaction_common SET STATUS='authstarted',remark="",paymentid='' WHERE trackingid=42468

    UPDATE transaction_common SET STATUS='authstarted',remark="",paymentid='' WHERE trackingid=42977

    UPDATE transaction_common SET STATUS='authstarted',remark="",paymentid='' WHERE trackingid=11902

    UPDATE transaction_common SET STATUS='markedforreversal',remark="",paymentid='' WHERE trackingid=42482

    SELECT  STATUS,remark,paymentid,fromtype,trackingid FROM transaction_common WHERE trackingid IN (42468,42977,11902,42482)

    SELECT trackingid, toid, STATUS, amount, description, remark, accountid, fromid, fromtype FROM transaction_common WHERE STATUS IN ('authstarted','capturestarted') AND fromtype='Borgun' AND (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15 AND trackingid IN (42468,42977,11902)

     */
}

