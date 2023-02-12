package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.paySafeCard.PaySafeCardUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/15/15
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaySafeCardBackEndServlet extends PzServlet
{
    private static Logger log = new Logger(PaySafeCardBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySafeCardBackEndServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.error("enter in PaySafeCardBackEndServlet");
        res.setContentType("text/html");
        String mTId = "";
        String serialNumbers = "";
        String amount = "";
        String paymentId = "";
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Connection con = null;

        transactionLogger.debug("-------Enter in doService of PaySafeCardBackEndServlet-------");

        for (Object key : req.getParameterMap().keySet())
        {
            log.debug("----for loop PaySafeCardBackEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.debug("----for loop PaySafeCardBackEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }


        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        PaySafeCardUtils paySafeCardUtils = new PaySafeCardUtils();
        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg = null;
        CommResponseVO commResponseVO = null;
        TransactionManager transactionManager = new TransactionManager();

        if (functions.isValueNull(req.getParameter("mtid")))
        {
            mTId = req.getParameter("mtid");
            serialNumbers = req.getParameter("serialNumbers");
            String status = "Failed";
            String[] param = serialNumbers.split(";");
            paymentId = param[0];
            amount = param[2];
            String message = "";
            String notificationStatus = "";
            String notificationRemark = "";
            int notificationCount = 0;


            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(mTId);

            if(transactionDetailsVO!=null)
            {

                auditTrailVO.setActionExecutorName("BackEndServlet");
                auditTrailVO.setActionExecutorId(transactionDetailsVO.getToid());

                try
                {
                    con = Database.getConnection();
                    pg = AbstractPaymentGateway.getGateway(transactionDetailsVO.getAccountId());
                    commResponseVO = (CommResponseVO) pg.processQuery(mTId, commRequestVO);
                    if (commResponseVO != null)
                    {
                        transactionLogger.debug("1st query status---" + commResponseVO.getStatus());
                        if (commResponseVO.getStatus().equalsIgnoreCase("success"))
                        {
                            transactionLogger.debug("transaction status for---" + commResponseVO.getTransactionStatus());
                            transactionLogger.debug("status for transaction---" + transactionDetailsVO.getStatus());


                            if (commResponseVO.getTransactionStatus().equalsIgnoreCase("S") && !transactionDetailsVO.getStatus().equalsIgnoreCase("capturesuccess"))
                            {
                        /*String aQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='authsuccessful' ,remark='Transaction Successful' where trackingid = " + mTId;
                        Database.executeUpdate(aQuery.toString(), con);
                        paymentManager.updatePaymentIdForPaySafeCard(paymentId, mTId, "authsuccessful");
                        entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                        paymentId = commResponseVO.getTransactionId();

                        String csQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturestarted' where trackingid = " + mTId;
                        transactionLogger.debug("capstarted query--"+csQuery);
                        Database.executeUpdate(csQuery.toString(), con);*/

                                entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                paymentManager.updatePaymentIdForCommon(paymentId, mTId, "capturestarted");
                                entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.ACTION_CAPTURE_STARTED, commResponseVO, auditTrailVO, null);

                                commRequestVO = paySafeCardUtils.getRequestDataForDabit(mTId, commResponseVO.getTransactionId());
                                CommResponseVO commResponseVO1 = (CommResponseVO) pg.processCapture(mTId, commRequestVO);
                                commResponseVO1.setTransactionId(paymentId);
                                transactionLogger.debug("status for capture---" + commResponseVO1.getStatus());
                                if (commResponseVO1.getStatus().equalsIgnoreCase("success"))
                                {
                                    notificationStatus = "capturesuccess";
                                    notificationRemark = "Transaction Successful";

                                    if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                                    {
                                        transactionLogger.error("---inside sending notification for paySafeCard ----" + transactionDetailsVO.getNotificationUrl());
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("customerEmail", transactionDetailsVO.getEmailaddr());
                                        TransactionUtility transactionUtility = new TransactionUtility();
                                        transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, mTId, notificationStatus, notificationRemark);
                                        notificationCount = 1;
                                        transactionLogger.error("notificationCount------" + notificationCount);
                                    }
                                    transactionLogger.debug("inside success after capture---");
                                    commResponseVO = (CommResponseVO) pg.processQuery(mTId, commRequestVO);
                                    transactionLogger.debug("inside success after capture---");

                                    status = "Successful";
                                    String sQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturesuccess' ,remark='Transaction Successful' ,notificationCount='" + notificationCount + "' where trackingid = " + mTId;
                                    Database.executeUpdate(sQuery.toString(), con);
                                    paymentManager.updateCapturesuccessForPaySafeCard(paymentId, mTId, "capturesuccess", amount);
                                    commResponseVO.setStatus("success");
                                    entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                }
                                else
                                {

                                    status = "failed";
                                    String afQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='capturefailed' ,remark='Capture Failed' where trackingid = " + mTId;
                                    Database.executeUpdate(afQuery.toString(), con);
                                    paymentManager.updatePaymentIdForCommon(paymentId, mTId, "capturefailed");
                                    entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, commResponseVO, auditTrailVO, null);

                                }
                            }
                        }
                        else
                        {
                            notificationStatus = "authfailed";
                            notificationRemark = "Transaction Successful";

                            if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                            {
                                transactionLogger.error("---inside sending notification for paySafeCard ----" + transactionDetailsVO.getNotificationUrl());
                                HashMap hashMap = new HashMap();
                                hashMap.put("customerEmail", transactionDetailsVO.getEmailaddr());
                                TransactionUtility transactionUtility = new TransactionUtility();
                                transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, mTId, notificationStatus, notificationRemark);
                                notificationCount = 1;
                                transactionLogger.error("notificationCount------" + notificationCount);
                            }
                            status = status + ":" + commResponseVO.getDescription();
                            message = "Transaction Failed";
                            paymentManager.updatePaymentIdForCommon(paymentId, mTId, "authfailed");
                            String aQuery = "update transaction_common set paymentid='" + commResponseVO.getTransactionId() + "',status='authfailed' ,remark='" + message + "',notificationCount='" + notificationCount + "' where trackingid = " + mTId;
                            Database.executeUpdate(aQuery.toString(), con);
                            entry.actionEntryForCommon(mTId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        }

                        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, mTId, status, null, null);

                    }
                }
                catch (PZDBViolationException e)
                {
                    log.error("error", e);
                    transactionLogger.error("error", e);
                    PZExceptionHandler.raiseAndHandleDBViolationException("PaySafeCardBackEndServlet", "doService()", null, "transaction", "Exception while update database for paysafecard", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), mTId, "BACKENDSERVLET");
                }
                catch (SystemError systemError)
                {
                    log.error("error", systemError);
                    transactionLogger.error("error", systemError);

                    PZExceptionHandler.raiseAndHandleDBViolationException("PaySafeCardBackEndServlet", "doService()", null, "transaction", "Exception while update database for paysafecard", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), mTId, "BACKENDSERVLET");
                }
                catch (PZConstraintViolationException e)
                {
                    log.error("error", e);
                    transactionLogger.error("error", e);
                    PZExceptionHandler.handleCVEException(e, mTId, "BACKENDSERVLET");
                }
                catch (PZTechnicalViolationException e)
                {
                    log.error("error", e);
                    transactionLogger.error("error", e);
                    PZExceptionHandler.handleTechicalCVEException(e, mTId, "BACKENDSERVLET");
                }
                catch (PZGenericConstraintViolationException e)
                {
                    log.error("error", e);
                    transactionLogger.error("error", e);
                    PZExceptionHandler.handleGenericCVEException(e, mTId, "BACKENDSERVLET");
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
        }
    }
}
