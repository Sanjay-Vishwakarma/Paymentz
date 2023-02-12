package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.borgun.core.BorgunErrorCodeClass;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.borgun.core.BorgunRequestVO;
import com.payment.borgun.core.BorgunResponseVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
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
 * Created by admin on 8/9/2016.
 */
public class BorgunMarkforReversalCron
{
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.KotakServlet");
    private static Logger logger = new Logger(BorgunMarkforReversalCron.class.getName());
    private static TransactionLogger transactionlogger = new TransactionLogger(BorgunMarkforReversalCron.class.getName());

    public static void main(String args[])throws SystemError
    {
        BorgunMarkforReversalCron borgunMarkforReversalCron = new BorgunMarkforReversalCron();
        String sResponse = borgunMarkforReversalCron.borgunMarkforReversalCron(new Hashtable());
    }

    public String borgunMarkforReversalCron(Hashtable hashtable)
    {
        logger.debug("Inside  borgunMarkforReversalCron method:::");

        StringBuffer success = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        //success.append("MarkedForReversal Transaction List" + "<br>");

        AbstractPaymentGateway abstractPaymentGateway = null;
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        ActionEntry entry = new ActionEntry();

        String trackingid = null;
        String sDescription = null;
        String accountid = null;
        String toid = null;
        String paymentid = "";
        String transStatus = "";
        String remark = "";
        String amount = "";
        String status = "";
        String currency = "";

        int markedforReversal = 0;
        int reversed = 0;
        int failedCounter = 0;
        int notProcessedCounter =0;
        int tAdd = 0;
        try
        {
            connection = Database.getConnection();
            String wCond = "FROM transaction_common WHERE STATUS='markedforreversal' AND fromtype='Borgun' AND (TIMESTAMPDIFF(HOUR, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 12 ";
            String selectQuery = "SELECT trackingid, toid, status, amount, description, remark, accountid, fromid, fromtype, currency "+wCond;
            String cQuery = "SELECT count(*) "+wCond;

            ps =  connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();

            ResultSet rs = Database.executeQuery(cQuery.toString(), connection);
            rs.next();
            markedforReversal = rs.getInt(1);

            logger.debug("Select Query Borgun---"+ps);
            while (resultSet.next())
            {
                trackingid = resultSet.getString("trackingid");
                accountid = resultSet.getString("accountid");
                toid = resultSet.getString("toid");
                amount = resultSet.getString("amount");
                sDescription = resultSet.getString("description");
                status = resultSet.getString("status");
                currency = resultSet.getString("currency");
                logger.debug("Result set---" + trackingid);

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("BorgunMarkforReversalCron");

                BorgunRequestVO requestVO = new BorgunRequestVO();
                BorgunResponseVO responseVO = null;
                BorgunResponseVO refundResponseVO = null;
                CommTransactionDetailsVO transactionDetailsVO=new CommTransactionDetailsVO();

                transactionDetailsVO.setOrderId(trackingid);
                requestVO.setTransDetailsVO(transactionDetailsVO);

                BorgunPaymentGateway borgunPaymentGateway = new BorgunPaymentGateway(accountid);

                responseVO = (BorgunResponseVO)borgunPaymentGateway.processInquiry(requestVO);
                transactionlogger.debug("inside in---" + responseVO.getStatus());

                if(responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    if(responseVO.getListMap()!=null)
                    {
                        if(responseVO.getListMap().get("sale")!=null && responseVO.getListMap().get("refund")==null)
                        {
                            transactionlogger.debug("inside sale --");
                            CommMerchantVO merchantAccountVO = new CommMerchantVO();
                            CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountid));

                            merchantAccountVO.setMerchantId(account.getMerchantId());

                            transDetailsVO.setAmount(amount);
                            transDetailsVO.setCurrency(currency);
                            transDetailsVO.setOrderId(String.valueOf(trackingid));
                            transDetailsVO.setOrderDesc("Refund By Cron");
                            transDetailsVO.setPreviousTransactionId(getPrevousTransId(trackingid));

                            requestVO.setCommMerchantVO(merchantAccountVO);
                            requestVO.setTransDetailsVO(transDetailsVO);

                            refundResponseVO = (BorgunResponseVO) borgunPaymentGateway.processRefund(trackingid,requestVO);

                            transactionlogger.debug("Refund response from BorgunMarkedforReversal---"+refundResponseVO.getStatus());

                            if(refundResponseVO.getStatus().equalsIgnoreCase("success"))
                            {
                                transStatus = "reversed";
                                paymentid = refundResponseVO.getListMap().get("refund").getAuthorizationCode();
                                refundResponseVO.setTransactionId(paymentid);
                                remark = BorgunErrorCodeClass.getErrorStringFromCode(refundResponseVO.getListMap().get("refund").getActionCode());
                                refundResponseVO.setRemark(remark);
                                refundResponseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, refundResponseVO, auditTrailVO, null);
                                reversed++;

                                yTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,"reversed"));
                            }
                        }
                        if(responseVO.getListMap().get("refund")!=null)
                        {
                            transactionlogger.debug("inside Refund --");
                            responseVO.setErrorCode(responseVO.getListMap().get("refund").getActionCode());
                            responseVO.setResponseTime(responseVO.getListMap().get("refund").getTransactionDate());
                            responseVO.setTransactionType("refund");
                            if(responseVO.getListMap().get("refund").getActionCode().equalsIgnoreCase("000"))
                            {
                                transStatus = "reversed";
                                paymentid = responseVO.getListMap().get("refund").getAuthorizationCode();
                                responseVO.setTransactionId(paymentid);
                                remark = BorgunErrorCodeClass.getErrorStringFromCode(responseVO.getListMap().get("refund").getActionCode());
                                responseVO.setRemark(remark);
                                responseVO.setDescription(remark);
                                //Detail Table Entry
                                entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, responseVO, auditTrailVO, null);
                                reversed++;

                                yTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,"reversed"));
                            }
                            else
                            {
                                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                                CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
                                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountid));

                                merchantAccountVO.setMerchantId(account.getMerchantId());

                                transDetailsVO.setAmount(amount);
                                transDetailsVO.setCurrency(currency);
                                transDetailsVO.setOrderId(String.valueOf(trackingid));
                                transDetailsVO.setOrderDesc("Refund By Cron");
                                transDetailsVO.setPreviousTransactionId(getPrevousTransId(trackingid));

                                requestVO.setCommMerchantVO(merchantAccountVO);
                                requestVO.setTransDetailsVO(transDetailsVO);

                                refundResponseVO = (BorgunResponseVO) borgunPaymentGateway.processRefund(trackingid,requestVO);

                                transactionlogger.debug("Refund response from BorgunMarkedforReversal---"+refundResponseVO.getStatus());

                                if(refundResponseVO.getStatus().equalsIgnoreCase("success"))
                                {
                                    transStatus = "reversed";
                                    paymentid = refundResponseVO.getListMap().get("refund").getAuthorizationCode();
                                    refundResponseVO.setTransactionId(paymentid);
                                    remark = BorgunErrorCodeClass.getErrorStringFromCode(refundResponseVO.getListMap().get("refund").getActionCode());
                                    refundResponseVO.setRemark(remark);
                                    refundResponseVO.setDescription(remark);
                                    //Detail Table Entry
                                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, refundResponseVO, auditTrailVO, null);
                                    reversed++;
                                }

                            }
                        }
                        //main table entry
                        insertMainTableEntry(transStatus,remark,paymentid,trackingid);

                        //update status flags
                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,transStatus, connection);
                    }
                    else
                    {
                        transactionlogger.debug("inside not processed 1 --");
                        notProcessedCounter++;
                        nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
                    }
                }
                else   //Transaction not found.
                {
                    transactionlogger.debug("inside not processed 1 --");
                    notProcessedCounter++;
                    nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
                }
            }
        }

        catch (SQLException e)
        {
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunMarkforReversalCron.class.getName(), "borgunMarkforReversalCron", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "Borgun Mark for Reversal cron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (PZTechnicalViolationException pzt)
        {
            logger.error("PZTechnicalViolationException exception",pzt);
            PZExceptionHandler.handleTechicalCVEException(pzt, toid, "BorgunMarkforReversalCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (PZDBViolationException pzd)
        {
            logger.error("PZDBViolationException",pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "BorgunMarkforReversalCron");
            notProcessedCounter++;
            nTransaction.append(setTableData(trackingid,accountid,toid,sDescription,currency,amount,status));
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(BorgunMarkforReversalCron.class.getName(), "borgunMarkforReversalCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "Borgun Mark for Reversal  cron");
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
        success.append("<b>Total Transactions in MarkedforReversal : </b>"+markedforReversal+"<br>");
        success.append("<br>");
        success.append("Total Refunded :"+reversed+"<br>");

        success.append("Total Not Processed :"+notProcessedCounter+"<br>");

        if(notProcessedCounter>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Transactions in MarkedforReversal</b></u><br>");
            success.append("<br>");
            sHeader.append(getTableHeader());
            success.append("<br>");
            sHeader.append("<br>");
            sHeader.append(nTransaction);
            success.append("<br>");
            sHeader.append("</table>");
        }

        if(reversed>0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Reversed Transactions </b></u><br>");
            success.append("<br>");
            sHeader.append(getTableHeader());
            success.append("<br>");
            sHeader.append("<br>");
            sHeader.append(yTransaction);
            success.append("<br>");
            sHeader.append("</table>");
        }

        success.append(sHeader);

        transactionlogger.debug("total count---"+success);
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_MARKEDFORREVERSAL_CRON_REPORT, "", "", success.toString(),null);

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

    public String getPrevousTransId(String trackingId)
    {
        Connection conn = null;
        String gateway_transactionid = "";
        try
        {
            conn = Database.getConnection();

            String transaction_details = "select responsetransactionid from transaction_common_details where trackingid=? and status='capturesuccess'";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setString(1, trackingId);
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            transactionlogger.debug("transDetailsprepstmnt from refund in CommonPaymentProcess----"+transDetailsprepstmnt);
            if (rsTransDetails.next())
            {
                gateway_transactionid = rsTransDetails.getString("responsetransactionid");
            }
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
            Database.closeConnection(conn);
        }
        return gateway_transactionid;
    }

}
