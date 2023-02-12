package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvision.core.PayVisionResponseVO;
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
 * Created by Admin on 1/17/18.
 */
public class PayVisionAuthStartedCron
{
    private static Logger logger = new Logger(PayVisionAuthStartedCron.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVisionAuthStartedCron.class.getName());
    boolean isLogEnabled= Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));

    public static void main(String args[]) throws SystemError
    {
        PayVisionAuthStartedCron payVisionAuthStartedCron = new PayVisionAuthStartedCron();
        String sResponse = payVisionAuthStartedCron.payVisionAuthStartedCron(new Hashtable());
    }

    public String payVisionAuthStartedCron(Hashtable hashtable)
    {
        if (isLogEnabled)
        logger.debug("::: Inside payvisionAuthStartedCron method :::");

        StringBuffer success = new StringBuffer();
        Functions functions= new Functions();
        StringBuffer nTransaction = new StringBuffer();
        PayVisionResponseVO responseVO = null;
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>payvision</b>" + "</u><br>");
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
        String timestamp=null;

        ActionEntry entry = new ActionEntry();
        int actionEntry = 0;

        int authstartedCounter = 0;
        int authsucessCounter = 0;
        int authFailedCounter = 0;
        int captureSuccesCounter = 0;
        int cancelledCounter = 0;
        int refundCounter = 0;
        int failedCounter = 0;
        int notProcessedCounter = 0;

        String transStatus = "";
        String paymentid = "";
        String remark = "";
        String transtype="";
        String detailsTableStatus="";


        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            connection = Database.getConnection();
            String wCond = "FROM transaction_common as t WHERE t.STATUS in ('authstarted','capturestarted') AND fromtype='PayVision' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP))>1";

            String selectQuery = "SELECT t.trackingid, t.toid, t.status, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;

            String cQuery = "SELECT count(*) " + wCond;
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            ResultSet rs = Database.executeQuery(cQuery.toString(), connection);
            rs.next();
            authstartedCounter = rs.getInt(1);

            if(isLogEnabled)
            transactionLogger.debug("Select Query payvision---" + ps+"----authstartedCounter and capturestartedCounter---"+authstartedCounter);

            while (resultSet.next())
            {
                trackingid = resultSet.getString("trackingid");
                amount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");

                String selectQuery1 = "SELECT status,timestamp,transtype FROM transaction_common_details WHERE trackingid=" + trackingid + " ORDER BY detailid desc limit 1";
                ps = connection.prepareStatement(selectQuery1);
                ResultSet resultSet1 = ps.executeQuery();

                if (isLogEnabled)
                    transactionLogger.debug("selectQuery1-----" + selectQuery1);

                if (resultSet1!=null)
                {
                    while (resultSet1.next())
                    {
                        if (functions.isValueNull(resultSet1.getString("transtype")))
                            transtype = resultSet1.getString("transtype");
                        if (functions.isValueNull(resultSet1.getString("status")))
                            detailsTableStatus = resultSet1.getString("status");
                        if (functions.isValueNull(resultSet1.getString("timestamp")))
                            timestamp = resultSet1.getString("timestamp");


                        auditTrailVO.setActionExecutorId(toid);
                        auditTrailVO.setActionExecutorName("payvisionAuthStartedCron");

                        CommRequestVO requestVO = new CommRequestVO();

                        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

                        commTransactionDetailsVO.setOrderId(trackingid);
                        commTransactionDetailsVO.setResponsetime(timestamp);

                        requestVO.setTransDetailsVO(commTransactionDetailsVO);
                        //requestVO.getCommMerchantVO().setMerchantId("108");
                        if (isLogEnabled)
                            transactionLogger.debug(" PayVision trackingid-------" + trackingid);

                        if (isLogEnabled)
                            transactionLogger.debug(" PayVision transtype-------" + transtype);

                        if (functions.isValueNull(transtype))
                        {
                            PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway(accountid);
                            if (transtype.equalsIgnoreCase(PZProcessType.AUTH.toString()) && "authstarted".equals(detailsTableStatus))
                            {
                                responseVO = (PayVisionResponseVO) payVisionPaymentGateway.processQuery(trackingid + "_A", requestVO);
                            }
                            else if (transtype.equalsIgnoreCase(PZProcessType.SALE.toString()) && "authstarted".equals(detailsTableStatus))
                            {
                                responseVO = (PayVisionResponseVO) payVisionPaymentGateway.processQuery(trackingid + "_S", requestVO);
                            }
                            else if (transtype.equalsIgnoreCase(PZProcessType.CAPTURE.toString()) && "capturestarted".equals(detailsTableStatus))
                            {
                                responseVO = (PayVisionResponseVO) payVisionPaymentGateway.processQuery(trackingid + "_C", requestVO);
                            }
                            else if (transtype.equalsIgnoreCase(PZProcessType.THREE_D_AUTH.toString()) && "3D_confirmation".equals(detailsTableStatus))
                            {
                                responseVO = (PayVisionResponseVO) payVisionPaymentGateway.processQuery(trackingid + "_3DA", requestVO);
                            }
                            else if (transtype.equalsIgnoreCase(PZProcessType.THREE_D_SALE.toString()) && "3D_confirmation".equals(detailsTableStatus))
                            {
                                responseVO = (PayVisionResponseVO) payVisionPaymentGateway.processQuery(trackingid + "_3DS", requestVO);
                            }

                            if (responseVO != null)
                            {
                                if (responseVO.getStatus().equalsIgnoreCase("success"))
                                {
                                    if (isLogEnabled)
                                        transactionLogger.debug("------ inside Approved-------");
                                    if (!functions.isValueNull(responseVO.getRemark()))
                                    {
                                        responseVO.setRemark("Transaction Successful");
                                    }

                                    if (responseVO.getTrackingMemberCode().contentEquals(trackingid + "_A"))
                                    {
                                        //Auth Success
                                        transStatus = "authsuccessful";
                                        paymentid = responseVO.getTransactionId();
                                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                        //Detail Table Entry
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        authsucessCounter++;
                                    }
                                    else if (responseVO.getTrackingMemberCode().contentEquals(trackingid + "_S"))
                                    {
                                        //Capture Success
                                        transStatus = "capturesuccess";
                                        paymentid = responseVO.getTransactionId();
                                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                        //Detail Table Entry
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        captureSuccesCounter++;
                                    }
                                    else if (responseVO.getTrackingMemberCode().contains(trackingid + "_C"))
                                    {
                                        //Captured
                                        transStatus = "capturesuccess";
                                        paymentid = responseVO.getTransactionId();
                                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                        //Detail Table Entry for capture started
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, responseVO, auditTrailVO, null);

                                        //Detail Table Entry for capture success
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        captureSuccesCounter++;

                                    }
                                    else if (responseVO.getTrackingMemberCode().contains(trackingid + "_3DA"))
                                    {
                                        //Captured
                                        transStatus = "authsuccessful";
                                        paymentid = responseVO.getTransactionId();
                                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                        //Detail Table Entry
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        authsucessCounter++;

                                    }
                                    else if (responseVO.getTrackingMemberCode().contains(trackingid + "_3DS"))
                                    {
                                        //Captured
                                        transStatus = "capturesuccess";
                                        paymentid = responseVO.getTransactionId();
                                        insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                        //Detail Table Entry for capture started
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, responseVO, auditTrailVO, null);

                                        //Detail Table Entry for capture success
                                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVO, null);
                                        captureSuccesCounter++;

                                    }
                                }
                                else if (responseVO.getStatus().equalsIgnoreCase("fail"))
                                {
                                    if (isLogEnabled)
                                        transactionLogger.debug("-----inside authfailed-----");
                                    //Declined transaction
                                    if (!functions.isValueNull(responseVO.getRemark()))
                                    {
                                        responseVO.setRemark("Transaction Failed");
                                    }

                                    transStatus = "authfailed";
                                    paymentid = responseVO.getTransactionId();
                                    insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                    //Detail Table Entry
                                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVO, null);
                                    authFailedCounter++;
                                }
                                else if (responseVO.getStatus().equalsIgnoreCase("notFound"))
                                {
                                    if (isLogEnabled)
                                        transactionLogger.debug("-----inside failed-----");
                                    //Not found transaction
                                    if (!functions.isValueNull(responseVO.getRemark()))
                                    {
                                        responseVO.setRemark("Transaction Not Found");
                                    }
                                    transStatus = "failed";
                                    paymentid = "";
                                    insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                    //Detail Table Entry
                                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                                    failedCounter++;
                                    yTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount, status));
                                }
                            }
                            else
                            {
                                if (isLogEnabled)
                                    transactionLogger.debug("-----inside failed ------");
                                //Not found transaction
                                responseVO = new PayVisionResponseVO();
                                responseVO.setRemark("Transaction Not Found");
                                transStatus = "failed";
                                paymentid = "";
                                insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, responseVO, auditTrailVO, null);
                                failedCounter++;
                                yTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount, status));
                            }
                        }
                        else
                        {
                            if (isLogEnabled)
                                transactionLogger.debug("-----inside failed-----");
                            //Not found transaction
                            responseVO = new PayVisionResponseVO();
                            responseVO.setRemark("Transaction Not Found");
                            transStatus = "authstarted";
                            paymentid = "";
                            insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                            //Detail Table Entry
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.ACTION_AUTHORISTION_STARTED, responseVO, auditTrailVO, null);
                            authstartedCounter++;
                            yTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount, status));
                        }
                        //update status flags

                    }
                }else {
                    if (isLogEnabled)
                        transactionLogger.debug("-----inside failed-----");
                    //Not found transaction
                    responseVO = new PayVisionResponseVO();
                    responseVO.setRemark("Transaction Not Found");
                    transStatus = "authstarted";
                    paymentid = "";
                    insertMainTableEntry(transStatus, responseVO.getRemark(), paymentid, trackingid);
                    //Detail Table Entry
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.ACTION_AUTHORISTION_STARTED, responseVO, auditTrailVO, null);
                    authstartedCounter++;
                    yTransaction.append(setTableData(trackingid, accountid, toid, sDescription, currency, amount, status));
                }
                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, transStatus);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayVisionAuthStartedCron.class.getName(), "PayVisionAuthStartedCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "PayVisionAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }

        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "PayVisionAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(PayVisionAuthStartedCron.class.getName(),"PayVisionAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"PayVisionAuthStartedCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));

        }catch(PZConstraintViolationException e){
            logger.error("PZConstraintViolationException",e);
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch(PZGenericConstraintViolationException ge){
            logger.error("PZGenericConstraintViolationException",ge);
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        finally
        {
            if(connection!=null)
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
        success.append("<b>Failed Transactions(Order Not found) : </b>"+failedCounter+"<br>");
        success.append("<b>Not Processed Transactions : </b>"+notProcessedCounter+"<br>");
        int tAdd = authsucessCounter+authFailedCounter+captureSuccesCounter+failedCounter+notProcessedCounter;
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

        if(isLogEnabled)
        transactionLogger.debug("total count---"+success);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);
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
}
