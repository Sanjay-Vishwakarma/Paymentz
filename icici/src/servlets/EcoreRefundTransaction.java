import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.Util;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;

import com.payment.ecore.core.EcorePaymentProcess;
import com.payment.ecore.core.request.EcoreRefundRequest;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
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
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/7/12
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreRefundTransaction extends HttpServlet
{
    static Logger logger = new Logger(EcoreRefundTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        res.setContentType("text/html");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;
        //Database db = null;
        String mailStatus="failed";
        String currency=null;
        String query = null;
        String icicimerchantid = null;
        String refundamount = null;
        String authamount = null;
        String captureamount = null;
        String description = null;
        String toid = null;
        String company_name = null;
        String contact_emails = null;
        String accountId = null;
        String cardholdername = null;
        List failList = new ArrayList();
        List successlist = new ArrayList();
        String[] icicitransidStr =null;
        MerchantDAO merchantDAO = new MerchantDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        String memberid="";
        Functions functions = new Functions();
        logger.debug(req.getParameter("refundamount_" + req.getParameterValues("trackingid")));

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to EcoreRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to EcoreRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        //Added for reversal mail
        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();
        Collection<Hashtable> listOfRefunds = null;
        Hashtable refundDetails = null;
        BigDecimal rfamt=null;
        String fraud="";
        String occure="";
        for (String icicitransid : icicitransidStr)
        {
            boolean refunded = false;
            StringBuffer mailbody = null;

            if (req.getParameter("toid_" + icicitransid) != null && !req.getParameter("toid_" + icicitransid).equals(""))
            {
                toid = req.getParameter("toid_" + icicitransid);
            }
            else
            {
                sErrorMessage.append("ToId is missing");
                failList.add(icicitransid + "<BR>");
                continue;
            }

            MerchantDetailsVO merchantDetailsVO = null;
            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
            }
            catch (PZDBViolationException e)
            {
                sErrorMessage.append("Error while fetching member details");
                failList.add(icicitransid + "<BR>");
                continue;
            }

            if(req.getParameter("refundamount_" + icicitransid)!= null)
            {
                refundamount = req.getParameter("refundamount_" + icicitransid);
                if(req.getParameter("refundamount_" + icicitransid)!=null && !req.getParameter("refundamount_" + icicitransid).equals(""))
                {
                    rfamt=new BigDecimal(req.getParameter("refundamount_" + icicitransid));
                }
                else
                {
                    sErrorMessage.append("Refund amount should not be empty.");
                    req.setAttribute("cbmessage", sErrorMessage.toString());
                    RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                fraud=req.getParameter("isFraud_"+icicitransid);
            }
            else
            {
                sErrorMessage.append("Selected refund amount field should not be empty or Invalid.");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                logger.debug("forwarding to EcoreRefundList");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;

            }
            try
            {
                Double.parseDouble(refundamount);

            }
            catch (NumberFormatException ex)
            {
                logger.error("Invalid refund amount " ,ex);
                sErrorMessage.append("ERROR while process Refund amount");
                failList.add(icicitransid+"<BR>");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("EcoreRefundTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(), PZTechnicalExceptionEnum.NUMBER_FORMAT_EXCEPTION,null,ex.getMessage(), ex.getCause(), toid, PZOperations.ADMIN_REFUND);
                logger.debug("forwarding to EcoreRefundList");
                RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if (!Functions.checkAccuracy(refundamount, 2))
            {
                logger.debug("Refund Amount should be 2 decimal places accurate." + refundamount);
                sErrorMessage.append("Refund Amount should be 2 decimal places accurate.");
                failList.add(icicitransid+"<BR>");
                continue;
            }

            if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                Hashtable transDetails = TransactionEntry.getTransDetails( icicitransid);
                String transactionDate = (String) transDetails.get("transactiondate");
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long d = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                int refundAllowedDays = Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());
                if (d > refundAllowedDays)
                {
                    sErrorMessage.append("Transaction has exceeds refund expiry");
                    failList.add(icicitransid + "<BR>");
                    continue;
                }
            }

            if (Functions.checkStringNull(icicitransid) != null)
            {
                try
                {
                    conn = Database.getConnection();
                    query = "select toid, amount,transaction_ecore.accountid,captureamount, refundamount, name,members.company_name,members.contact_emails from transaction_ecore,members where status IN ('settled','capturesuccess') and transaction_ecore.toid=members.memberid and trackingid=?";

                    PreparedStatement p= conn.prepareStatement(query);
                    p.setString(1,icicitransid);
                    ResultSet rs = p.executeQuery();

                    if (rs.next())
                    {

                        //icicitransid = rs.getString("trackingid");
                        //fromid=rs.getString("fromid");
                        toid = rs.getString("toid");
                        company_name = rs.getString("company_name");
                        authamount = rs.getString("amount");
                        captureamount = rs.getString("captureamount");
                        refundamount = rs.getString("refundamount");
                        accountId = rs.getString("accountid");
                        cardholdername = rs.getString("name");
                        contact_emails = rs.getString("contact_emails");
                        //paymentorder=rs.getString("qwipiPaymentOrderNumber");
                        //toid = rs.getString("toid");
                        //String transid= rs.getString("transid");
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                        icicimerchantid = account.getMerchantId();
                        currency=account.getCurrency();
                        EcoreRefundRequest refundRequest= new EcoreRefundRequest();
                        if(fraud!=null && fraud.equals("Y"))
                        {
                            occure="Refund BY ADMIN (FRAUD Transaction)"; //set in VO object
                            description = "Refund of " + icicitransid +"  (Fraudulent Transaction)";
                            refundRequest.setFraud(true);
                        }
                        if (fraud!=null && fraud.equals("N") )
                        {
                            occure="Refund BY ADMIN";
                            description = "Refund of " + icicitransid ;
                            refundRequest.setFraud(false);
                        }

                        if (Double.parseDouble(refundamount) > Double.parseDouble(captureamount))
                        {
                            logger.debug("Refund Amount " + refundamount + " is greater than capture amount " + captureamount);
                            sErrorMessage.append("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+icicitransid);
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                        AbstractPaymentProcess payment  = new EcorePaymentProcess();

                        refundRequest.setMemberId(Integer.valueOf(toid));
                        refundRequest.setAccountId(Integer.parseInt(accountId));
                        refundRequest.setTrackingId(Integer.parseInt(icicitransid));
                        refundRequest.setRefundAmount(refundamount);
                        refundRequest.setRefundReason(occure);

                        //newly added
                        auditTrailVO.setActionExecutorId(toid);
                        auditTrailVO.setActionExecutorName("Admin Refund");
                        refundRequest.setAuditTrailVO(auditTrailVO);

                        PZRefundResponse response = payment.refund(refundRequest) ;
                        PZResponseStatus status = response.getStatus();
                        if(PZResponseStatus.ERROR.equals(status))                       {
                            throw new Exception();
                        }
                        else if(PZResponseStatus.FAILED.equals(status)){
                            throw new SystemError();
                        }

                        if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS) )
                        {
                            refunded = true;
                            mailStatus="successful";
                            successlist.add(icicitransid);
                            // preparing collections of refunds as per merchant
                            refundDetails = new Hashtable();
                            refundDetails.put("icicitransid",icicitransid);
                            refundDetails.put("captureamount",captureamount);
                            refundDetails.put("refundamount",refundamount);
                            refundDetails.put("description",response.getResponseDesceiption());
                            refundDetails.put("accountid",accountId);
                            refundDetails.put("cardholdername",cardholdername);
                            if(fraud!=null && fraud.equals("Y"))
                            {
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(icicitransid,"fraud",conn);
                            }
                            if(fraud!=null && fraud.equals("N"))
                            {
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(icicitransid,"reversed",conn);
                            }
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

                        }
                        else
                        {
                            sErrorMessage.append("<BR>Error while Process refund Transaction. TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                    }
                }
                catch (SystemError se)
                {
                    logger.error("Error while reversal :" ,se);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("EcoreRefundTransaction.java", "doPost()", null, "admin", "SQL Exception Thrown:::",null, se.getMessage(), se.getCause(), toid, PZOperations.ADMIN_REFUND);
                    sErrorMessage.append("<BR>Transaction is not found while Process refund TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                    failList.add(icicitransid+"<BR>");
                    continue;
                }
                catch (Exception e)
                {
                    logger.error("Error while reversal :",e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("EcoreRefundTransaction.java", "doPost()", null, "admin", "Error While reversal:::",null, e.getMessage(), e.getCause(), toid, PZOperations.ADMIN_REFUND);
                    failList.add(icicitransid+"<BR>");
                    continue;
                }//try catch ends
                finally {
                    Database.closeConnection(conn);
                }
            }
            SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
            sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION,icicitransid,mailStatus,description,null);
            AsynchronousSmsService smsService=new AsynchronousSmsService();
            smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION,icicitransid,mailStatus,description,null);

        }
        sErrorMessage.append("Fail reversed transaction list:" + Functions.commaseperatedString(Util.getStringArrayFromVector(failList)));

        sSuccessMessage.append("<br>Successful reversed transaction list: "+Functions.commaseperatedString(Util.getStringArrayFromVector(successlist))+"<BR>");

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreRefundList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}

