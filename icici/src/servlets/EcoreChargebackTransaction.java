import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/24/13
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreChargebackTransaction extends HttpServlet
{
    static Logger logger = new Logger(EcoreChargebackTransaction.class.getName());
    private static String OLD_MerchantId = ApplicationProperties.getProperty("ICICI_OLD_MERCHANTID");
    private static List listOfOldMerchantIDs = Functions.getVectorFromArray(Functions.convertCommaseperatedStringtoStringarr(OLD_MerchantId));

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Transaction transaction = new Transaction();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;
        TransactionEntry transactionEntry = null;

        sErrorMessage.append("<center><table border=2 BORDERCOLOR=white>");
        sErrorMessage.append("<tr colspan=8>");
        sErrorMessage.append("<td colspan=2 align=center bgcolor=gray>Tracking Id </td>");
        sErrorMessage.append("<td colspan=2 align=center bgcolor=gray>Status</td>");
        sErrorMessage.append("<td colspan=4 align=center bgcolor=gray>Description</td>");
        sErrorMessage.append("</tr>");

        String cbamount = null;
        String description = null;
        String toid = null;
        String cbreason=null;
        String cbremark=null;
        String accountId = null;
        String mailStatus="Fail";
        List failList = new ArrayList();
        List successlist = new ArrayList();
        String[] trackingid = req.getParameterValues("trackingid");
        if (Functions.checkArrayNull(trackingid) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();
        Functions functions = new Functions();
        Collection<Hashtable> listOfRefunds = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        Hashtable refundDetails = null;

        for (String icicitransid : trackingid)
        {
            boolean chargeback = false;
            StringBuffer mailbody = null;
            MerchantDetailsVO merchantDetailsVO = null;

            if (req.getParameter("toid_" + icicitransid) != null && !req.getParameter("toid_" + icicitransid).equals(""))
            {
                toid = req.getParameter("toid_" + icicitransid);
            }
            else
            {
                sErrorMessage.append(getMessage(icicitransid, "Failed", "ToId is missing."));
                continue;
            }

            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
            }
            catch (PZDBViolationException e)
            {
                sErrorMessage.append(getMessage(icicitransid, "Failed", "Error while fetching member details"));
                continue;
            }

            cbamount = req.getParameter("chargebackamount_" + icicitransid);
            cbreason = req.getParameter("chargebackreason_" + icicitransid);
            cbremark = req.getParameter("chargebackremark_" + icicitransid);
            accountId = req.getParameter("accountid_" + icicitransid);

            if(cbreason.equals("") || cbreason==null)
            {
                cbreason="N/A";
            }
            try
            {
                Double.parseDouble(cbamount);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Invalid Chargeback amount " ,ex);
                //sErrorMessage.append("ERROR while process chargeback amount");
                PZExceptionHandler.raiseAndHandleGenericViolationException("EcoreChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, ex.getMessage(), ex.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                sErrorMessage.append(getMessage(icicitransid,"Failed","ERROR while process chargeback amount"));
                failList.add(icicitransid+"<BR>");
                continue;
            }
            if (!Functions.checkAccuracy(cbamount, 2))
            {
                logger.debug("Chargeback Amount should be 2 decimal places accurate." + cbamount);
                //sErrorMessage.append("Chargeback Amount should be 2 decimal places accurate.");
                sErrorMessage.append(getMessage(icicitransid,"Failed","Chargeback Amount should be 2 decimal places accurate."));
                failList.add(icicitransid+"<BR>");
                continue;
            }

            if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                Hashtable transDetails = TransactionEntry.getTransDetails( icicitransid);
                String transactionDate = (String) transDetails.get("transactiondate");
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long d = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                int chargebackAllowedDays = Integer.parseInt(merchantDetailsVO.getChargebackAllowedDays());
                if (d > chargebackAllowedDays)
                {
                    sErrorMessage.append(getMessage(icicitransid, "Failed", "Transaction has exceeds refund expiry"));
                    continue;
                }
            }

            String icicitrakingid=null;
            String fromid=null;
            String amount=null;
            String status=null;
            String emailaddr=null;
            String qordernumber=null;
            String md5string=null;
            String md5Info=null;
            AuditTrailVO auditTrailVO=new AuditTrailVO();
            if (Functions.checkStringNull(icicitransid) != null)
            {
                try
                {
                    conn = Database.getConnection();
                    transactionEntry = new TransactionEntry();

                    String qry="select q.trackingid,q.transid,q.toid,q.fromid,q.description,q.amount,q.accountid,q.currency,M.reversalcharge,q.status,q.timestamp,q.name,q.ecorePaymentOrderNumber,M.contact_emails,M.company_name from transaction_ecore as q, members as M where q.toid=M.memberid and q.trackingid=?" ;
                    PreparedStatement pstmt=conn.prepareStatement(qry);
                    pstmt.setString(1,icicitransid);
                    ResultSet rs=pstmt.executeQuery();

                    if(rs.next())
                    {
                        icicitrakingid=rs.getString("trackingid");
                        toid=rs.getString("toid");
                        fromid=rs.getString("fromid");
                        description=rs.getString("description");
                        amount=rs.getString("amount");
                        status=rs.getString("status");
                        emailaddr=rs.getString("contact_emails");
                        accountId=rs.getString("accountid");
                        qordernumber=rs.getString("ecorePaymentOrderNumber");
                        BigDecimal amt= new BigDecimal(cbamount);

                        if (Double.parseDouble(cbamount) > Double.parseDouble(amount))
                        {
                            logger.debug("Chargeback Amount " + cbamount + " is greater than capture amount " + amount);

                            //sErrorMessage.append("Chargeback Amount " + cbamount + " is greater than capture amount");
                            sErrorMessage.append(getMessage(icicitransid,"Failed","Chargeback Amount" + cbamount +" is greater than capture amount"));
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                        //When Ecore Inquiry operation returns the transaction status, we need to implement gateway inquiry call here.
                        //This inquiry operation should check the transaction status in Ecore system and then should populate following boolean.
                        boolean toBeChargedback = true;
                         auditTrailVO.setActionExecutorId(toid);
                        auditTrailVO.setActionExecutorName("Admin");
                        if(toBeChargedback)
                        {
                            if(status.equals("capturesuccess"))
                            {
                                transactionEntry.newGenericCreditTransaction(icicitransid,amt,accountId,null,auditTrailVO);
                            }
                            if(status.equals("markedforreversal"))
                            {
                                //TODO:change the status to reversed in transaction_ecore
                                String query="update transaction_ecore set status='reversed' where status='markedforreversal' and trackingid=?";
                                PreparedStatement p1=conn.prepareStatement(query);
                                p1.setString(1,icicitransid);
                                int rows = p1.executeUpdate();
                            }

                            transaction.genericProcessChargeback(icicitransid,null ,null,cbamount,cbreason,null,null,null,auditTrailVO);
                            // preparing collections of refunds as per merchant
                            chargeback = true;
                            mailStatus="Successful";
                            refundDetails = new Hashtable();
                            refundDetails.put("icicitransid",icicitransid);
                            refundDetails.put("captureamount",amount);
                            refundDetails.put("chargebackamount",cbamount);
                            refundDetails.put("accountid",accountId);
                            successlist.add(icicitransid);
                            if(membersMap.get(toid)==null)
                            {
                                listOfRefunds = new ArrayList<Hashtable>();
                                listOfRefunds.add(refundDetails);
                                membersMap.put(toid,listOfRefunds);
                            }
                            else
                            {
                                listOfRefunds = membersMap.get(toid);
                                listOfRefunds.add(refundDetails);
                                membersMap.put(toid,listOfRefunds);
                            }
                            sErrorMessage.append(getMessage(icicitransid,"Success","Transaction Chargeback successfully"));
                        }
                        else
                        {
                            //sErrorMessage.append("<BR>Transaction found but unable to fetch the details from bank end. Try Chargebacking the transction later. TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            sErrorMessage.append(getMessage(icicitransid,"Failed","Transaction found but unable to fetch the details from bank end"));
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                    }
                    else
                    {
                        sErrorMessage.append(getMessage(icicitransid,"Failed","Transaction is not found in our system"));
                        failList.add(icicitransid+"<BR>");
                        continue;
                    }
                    //If chargeback did not happen successfully then send mail to admin to chargeback transaction manually

                }
                catch (SQLException e)
                {
                    logger.error("ERROR while serching transaction",e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("EcoreChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, e.getMessage(), e.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                    sErrorMessage.append(getMessage(icicitransid,"Failed","SQL Error while performing chargeback transaction"));
                    failList.add(icicitransid+"<BR>");
                    continue;
                }
                catch (SystemError se)
                {
                    logger.error("SystemError",se);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("EcoreChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, se.getMessage(), se.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                    sErrorMessage.append(getMessage(icicitransid,"Failed","System Error while performing chargeback transaction"));
                    failList.add(icicitransid+"<BR>");
                    continue;
                }
                finally
                {
                    Database.closeConnection(conn);
                    transactionEntry.closeConnection();
                }
                SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.CHARGEBACK_TRANSACTION,icicitransid,mailStatus,cbreason,null);
            }
        }

        //sErrorMessage.append("Fail chargeback transaction list: <BR> " + Functions.commaseperatedString(Util.getStringArrayFromVector(failList)));
        //sSuccessMessage.append("<BR>Successful chargeback transaction list:"+Functions.commaseperatedString(Util.getStringArrayFromVector(successlist))+"<br> ");

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        //logger.debug("forwarding to member preference"+chargeBackMessage.toString());

        RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreChargebackList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public String getMessage(String trackingId,String status,String description)
    {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("<tr colspan=8>");
        stringBuffer.append("<td colspan=2>"+trackingId+"</td>");
        stringBuffer.append("<td colspan=2>"+status+"</td>");
        stringBuffer.append("<td colspan=4>"+description+"</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }
}


