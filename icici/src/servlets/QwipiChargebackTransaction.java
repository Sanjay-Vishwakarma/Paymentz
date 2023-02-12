import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.QwipiAddressDetailsVO;
import com.directi.pg.core.valueObjects.QwipiRequestVO;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.directi.pg.core.valueObjects.QwipiTransDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.operations.PZOperations;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
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
public class QwipiChargebackTransaction extends HttpServlet
{
    static Logger logger = new Logger(QwipiChargebackTransaction.class.getName());
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

        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String action=req.getParameter("submitbutton");
        Transaction transaction = new Transaction();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;
        TransactionEntry transactionEntry = null;

        sErrorMessage.append("<center><table border=2>");
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
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiChargebackList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        //Update The Retrival Request Flag In bin_details
        if("RetrivalRequest Selected".equalsIgnoreCase(action))
        {
            TransactionDAO transactionDAO=new TransactionDAO();
            for (String icicitransid : trackingid)
            {
                String retrivalRequestFlag=req.getParameter("isRetrivalRequest_"+icicitransid);
                boolean status=transactionDAO.updateTransactionRetrialRequest(icicitransid,retrivalRequestFlag);
                if(status)
                {
                    sErrorMessage.append(getMessage(icicitransid,"Success","Record Updated Successfully."));
                }
                else
                {
                    sErrorMessage.append(getMessage(icicitransid,"Failed","Failed During Updation."));
                }
            }
        }
        else
        {
            //GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
            QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
            QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
            QwipiRequestVO requestDetail=null;
            QwipiResponseVO transRespDetails=null;
            HashMap<String,Collection> membersMap = new HashMap<String,Collection>();
            Collection<Hashtable> listOfRefunds = null;
            MerchantDAO merchantDAO = new MerchantDAO();

            Hashtable refundDetails = null;
            for (String icicitransid : trackingid)
            {
                boolean chargeback = false;
                StringBuffer mailbody = null;
                Functions functions=new Functions();
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

                if(!functions.isValueNull("chargebackamount_"+icicitransid)|| !ESAPI.validator().isValidInput("chargebackamount_" + icicitransid, req.getParameter("chargebackamount_" + icicitransid), "AmountStr", 10, false))
                {
                    sErrorMessage.append(getMessage(icicitransid,"Failed","Invalid chargeback amount."));
                    continue;
                }
                if(!ESAPI.validator().isValidInput("chargebackremark_"+icicitransid, req.getParameter("chargebackremark_" + icicitransid), "SafeString", 50, true))
                {
                    sErrorMessage.append(getMessage(icicitransid,"Failed","Invalid chargeback remark."));
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
                    PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, ex.getMessage(), ex.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                    sErrorMessage.append(getMessage(icicitransid,"Failed","ERROR while process chargeback amount."));
                    continue;
                }
                if (!Functions.checkAccuracy(cbamount, 2))
                {
                    logger.debug("Chargeback Amount should be 2 decimal places accurate." + cbamount);
                    sErrorMessage.append(getMessage(icicitransid,"Failed","Chargeback Amount should be 2 decimal places accurate."));
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
                        String qry="select q.trackingid,q.transid,q.toid,q.fromid,q.description,q.amount,q.accountid,q.currency,M.reversalcharge,q.status,q.timestamp,q.name,q.qwipiPaymentOrderNumber,M.contact_emails,M.company_name from transaction_qwipi as q, members as M where q.toid=M.memberid and q.trackingid=?" ;
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
                            qordernumber=rs.getString("qwipiPaymentOrderNumber");
                            BigDecimal amt= new BigDecimal(cbamount);

                            if (Double.parseDouble(cbamount) > Double.parseDouble(amount))
                            {
                                logger.debug("Chargeback Amount " + cbamount + " is greater than capture amount " + amount);
                                sErrorMessage.append(getMessage(icicitransid,"Failed","Chargeback Amount " + cbamount + " is greater than capture amount."));
                                continue;
                            }

                            qry="select midkey from gateway_accounts_qwipi where mid=? and accountid=?";

                            //conn= Database.getConnection();
                            PreparedStatement p= conn.prepareStatement(qry);
                            p.setString(1,fromid);
                            p.setString(2,accountId);
                            rs =p.executeQuery();
                            if(rs.next())
                            {
                                md5string=rs.getString("midkey");
                            }

                            md5Info=Functions.convertmd5(fromid+description+md5string);
                            AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                            AddressDetail.setMd5info(md5Info);
                            TransDetail.setMerNo(fromid);
                            TransDetail.setOrderDesc(description);
                            requestDetail = new QwipiRequestVO(null,AddressDetail, TransDetail);
                            transRespDetails = (QwipiResponseVO) paymentGateway.processInquiry(requestDetail);
                            auditTrailVO.setActionExecutorId(toid);
                            auditTrailVO.setActionExecutorName("Admin");
                            //if(transRespDetails.getResult().equals("O"))
                            if(transRespDetails.getResult().equals("O"))
                            {

                                //if(transRespDetails.getCbCode().equals("Y"))
                                if(transRespDetails.getCbCode().equals("Y"))
                                {

                                    if(status.equals("capturesuccess"))
                                    {
                                        int paymentTransId=0;
                                        paymentTransId = transactionEntry.newGenericCreditTransaction(icicitransid,amt,accountId,null,auditTrailVO);
                                    }
                                    if(status.equals("markedforreversal"))
                                    {
                                        //TODO:change the status to reversed in transaction_qwipi
                                        String query="update transaction_qwipi set status='reversed' where status='markedforreversal' and trackingid=?";
                                        PreparedStatement p1=conn.prepareStatement(query);
                                        p1.setString(1,icicitransid);
                                        int rows = p1.executeUpdate();
                                    }
                                    transaction.genericProcessChargeback(icicitransid,null ,null,cbamount,cbreason,null,transRespDetails,null,auditTrailVO);
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
                                    sErrorMessage.append(getMessage(icicitransid,"Success","Chargeback Successfully"));
                                }
                                else
                                {
                                    sErrorMessage.append(getMessage(icicitransid,"Failed","Transaction found but it is not in chargeback status at Bank end."));
                                    continue;
                                }
                            }
                            else
                            {
                                sErrorMessage.append(getMessage(icicitransid,"Failed","Transaction found but unable to fetch the details from bank end."));
                                continue;
                            }
                        }
                        else
                        {
                            sErrorMessage.append(getMessage(icicitransid,"Failed","Transaction is not found in our system."));
                            continue;
                        }
                        //If chargeback did not happen successfully then send mail to admin to chargeback transaction manually

                    }
                    catch (SQLException e)
                    {
                        logger.error("ERROR while serching transaction",e);
                        PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, e.getMessage(), e.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","SQL Error while performing chargeback transaction."));
                        continue;
                    }
                    catch (SystemError se)
                    {
                        logger.error("SystemError",se);
                        PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, se.getMessage(), se.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","System Error while performing chargeback transaction."));
                        continue;
                    }
                    catch (NoSuchAlgorithmException e)
                    {
                        logger.error("NoSuchAlgorithmException",e);
                        PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiChargebackTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, e.getMessage(), e.getCause(),null, PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","NoSuchAlgorithm Error while performing chargeback transaction."));
                        continue;
                    }
                    catch (PZTechnicalViolationException e)
                    {
                        logger.error("NoSuchAlgorithmException",e);
                        PZExceptionHandler.handleTechicalCVEException(e,toid,PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","NoSuchAlgorithm Error while performing chargeback transaction."));
                        continue;
                    }
                    catch (PZConstraintViolationException e)
                    {
                        logger.error("NoSuchAlgorithmException",e);
                        PZExceptionHandler.handleCVEException(e,toid,PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","NoSuchAlgorithm Error while performing chargeback transaction."));
                        continue;
                    }
                    catch (PZDBViolationException e)
                    {
                        logger.error("NoSuchAlgorithmException",e);
                        PZExceptionHandler.handleDBCVEException(e,toid,PZOperations.ADMIN_CHARGEBACK);
                        sErrorMessage.append(getMessage(icicitransid,"Failed","NoSuchAlgorithm Error while performing chargeback transaction."));
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
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("sErrorMessage", chargeBackMessage.toString());
        logger.debug("forwarding to qwipichargebacklist"+chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/qwipichargebacklist.jsp?ctoken="+user.getCSRFToken());
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