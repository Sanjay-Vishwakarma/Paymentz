import com.directi.pg.*;
import com.fraud.FraudTransaction;
import com.fraud.FraudUtils;
import com.logicboxes.util.Util;
import com.manager.dao.FraudTransactionDAO;

import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 10/6/14
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReverseFraudTransactions extends HttpServlet
{
    private static Logger logger = new Logger(ReverseFraudTransactions.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        res.setContentType("text/html");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String[] icicitransidStr =null;
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        FraudUtils fraudUtils=new FraudUtils();
        FraudTransaction fraudTransaction=new FraudTransaction();
        FraudTransactionDAO transactionDAO=new FraudTransactionDAO();
        Hashtable refundDetails=null;
        List failList = new ArrayList();
        List successList = new ArrayList();

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid Tracking ID.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to fraudTransactionList");
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to fraudTransactionreverseList");
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String memberId=null;
        for (String icicitransid : icicitransidStr)
        {
            String accountId=null;
            String rfamt=null;
            String reversedAmount=null;
            String status=null;

            if(Functions.checkStringNull(icicitransid) != null)
            {
                accountId=req.getParameter("accountid_" + icicitransid);
                memberId=req.getParameter("memberid_" + icicitransid);
                rfamt=req.getParameter("refundamount_" + icicitransid);
                reversedAmount=req.getParameter("reversedamount_" + icicitransid);
                status=req.getParameter("status_" + icicitransid);
            }
            else
            {
                failList.add(icicitransid);
                continue;
            }
            if (Functions.checkStringNull(icicitransid) != null && Functions.checkStringNull(accountId) != null && Functions.checkStringNull(rfamt) != null)
            {
                String tableName=fraudUtils.getTableNameFromAccountId(accountId);
                if("transaction_common".equals(tableName))
                {
                    try
                    {
                        refundDetails=fraudTransaction.reverseForCommon(icicitransid,memberId, accountId,rfamt,reversedAmount, "Fraud Transaction",status);
                        if(refundDetails!=null)
                        {
                            successList.add((String)refundDetails.get("trackingid"));
                            logger.debug("Reversed Successfully form transaction_common======trackingId"+(String)refundDetails.get("trackingid"));
                        }

                    }
                    catch (SystemError e)
                    {
                        failList.add(icicitransid);
                        logger.debug("Reverse Error from transaction_common======trackingId"+icicitransid+"COZ===="+e);
                    }
                }
                if("transaction_ecore".equals(tableName))
                {
                    try
                    {
                        refundDetails=fraudTransaction.reverseForEcore(memberId,accountId,icicitransid,rfamt,"Farud Reason");
                        if(refundDetails!=null)
                        {
                            successList.add((String)refundDetails.get("trackingid"));
                            logger.debug("Reversed Successfully form transaction_ecore======trackingId"+(String)refundDetails.get("trackingid"));
                        }
                    }
                    catch (SystemError e)
                    {
                        failList.add(icicitransid);
                        logger.debug("Reverse Error from transaction_ecore======trackingId"+icicitransid+"COZ===="+e);
                    }
                }
                if("transaction_qwipi".equals(tableName))
                {
                    try
                    {
                        refundDetails=fraudTransaction.reverseQwipiTransaction(icicitransid,rfamt);
                        if(refundDetails!=null)
                        {
                            successList.add((String)refundDetails.get("trackingid"));
                            logger.debug("Reversed Successfully form transaction_qwipi======trackingId"+(String)refundDetails.get("trackingid"));
                        }

                    }
                    catch (SystemError e)
                    {
                        failList.add(icicitransid);
                        logger.debug("Reverse Error from transaction_qwipi======trackingId"+icicitransid+"COZ===="+e);
                    }
                }
            }
        }
        if(successList.size()>0)
        {
          transactionDAO.updateIsReversedStatus(successList);
            try
            {
                HashMap cbCountRecord = new HashMap();
                MailService mailService = new MailService();
                FraudTransactionDAO fraudTransactionDAO = new FraudTransactionDAO();
                logger.error("memberId--->"+memberId);
                Map<String, Map<String, String>> transactiondetails = fraudTransactionDAO.getHighRiskRefundTransactionDetailsForAlertPerTrackingId(successList);
                if (transactiondetails != null && transactiondetails.size() > 0)
                {
                cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTable(transactiondetails));
                cbCountRecord.put(MailPlaceHolder.TOID, memberId);
                    mailService.sendMail(MailEventEnum.HIGH_RISK_REFUND_TRANSACTION_INTIMATION, cbCountRecord);
                    fraudTransactionDAO.updateIsAlertSentForRefund((Set) successList);
                }
            }catch (Exception e)
            {
                logger.error("Exception---->",e);
            }
        }
        sSuccessMessage.append("<BR>Successful reversed transaction list: <BR>  "+Functions.commaseperatedString(Util.getStringArrayFromVector(successList))+"<br> ");
        sErrorMessage.append("<BR>Fail reversed transaction list: <BR>  " + Functions.commaseperatedString(Util.getStringArrayFromVector(failList))+"<BR>");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage",chargeBackMessage.toString());
        logger.debug("forwarding to fraudTransactionReverseList");
        RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}

