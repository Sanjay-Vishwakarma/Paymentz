import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.Util;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;

import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
 * Date: 12/7/12
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiRefundTransaction extends HttpServlet
{
    private static Logger logger = new Logger(QwipiRefundTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        Transaction transaction = new Transaction();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        Connection conn = null;
        TransactionEntry transactionEntry = null;

        try
        {
            conn = Database.getConnection();
            transactionEntry = new TransactionEntry();
        }
        catch (SystemError se)
        {
            logger.error("Error while reversal :",se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", "SQL Exception Thrown:::",null, se.getMessage(), se.getCause(), null, PZOperations.ADMIN_REFUND);
            sErrorMessage.append("Error while connecting to Database.");
            req.setAttribute("sErrorMessage", sErrorMessage.toString());
            logger.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiRefundList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        Database db = null;
        String query = null;
        String icicimerchantid = null;
        String refundamount = null;
        String authamount = null;
        String captureamount = null;
        String description = null;
        String authid = null;
        String currency=null;
        String captureid = null;
        String toid = null;
        String company_name = null;
        String contact_emails = null;
        String accountId = null;
        String cardholdername = null;
        String paymentorder=null;
        String billno=null;
        BigDecimal bdConst = new BigDecimal("0.01");
        List failList = new ArrayList();
        List successlist = new ArrayList();
        QwipiRequestVO RequestDetail=null;
        QwipiResponseVO transRespDetails=new QwipiResponseVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
        String[] icicitransidStr =null;
        String fromid=null;
        String fixamt=null;
        String mailStatus="failed";
        MerchantDAO merchantDAO = new MerchantDAO();

        //  AuditTrailVO Details
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId(icicimerchantid);
        auditTrailVO.setActionExecutorName("Admin");

        logger.debug(req.getParameter("refundamount_" + req.getParameterValues("trackingid")));

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("sErrorMessage", sErrorMessage.toString());
            logger.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiRefundList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("sErrorMessage", sErrorMessage.toString());
            logger.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiRefundList?ctoken="+user.getCSRFToken());
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
            Functions functions=new Functions();

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

            refundamount = req.getParameter("refundamount_" + icicitransid);
            if(!functions.isValueNull("refundamount_" + icicitransid)|| !ESAPI.validator().isValidInput("refundamount_" + icicitransid, req.getParameter("refundamount_" + icicitransid), "AmountStr", 10, false))
            {
                failList.add("Invalid refund amount for TrackingID:"+icicitransid+"<BR>");
                continue;
            }
            else
            {
                rfamt=new BigDecimal(req.getParameter("refundamount_" + icicitransid));
                try
                {
                    Double.parseDouble(refundamount);

                }
                catch (NumberFormatException ex)
                {
                    logger.error("Invalid refund amount " ,ex);
                    failList.add("Invalid refund amount for TrackingID:"+icicitransid+"<BR>");
                    PZExceptionHandler.raiseAndHandleTechnicalViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(), PZTechnicalExceptionEnum.NUMBER_FORMAT_EXCEPTION, null,ex.getMessage(), ex.getCause(), toid, PZOperations.ADMIN_REFUND);
                    continue;
                }
            }

            fraud=req.getParameter("isFraud_"+icicitransid);


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
                    query = "select trackingid,toid,fromid,description,amount,transid,transaction_qwipi.accountid,status,timestamp,name,qwipiPaymentOrderNumber,members.contact_emails,members.company_name from transaction_qwipi,members where status IN ('settled','capturesuccess') and transaction_qwipi.toid=members.memberid and trackingid=?";
                    PreparedStatement p= conn.prepareStatement(query);
                    p.setString(1,icicitransid);
                    ResultSet rs = p.executeQuery();

                    if (rs.next())
                    {

                        icicitransid = rs.getString("trackingid");
                        fromid=rs.getString("fromid");
                        toid = rs.getString("toid");
                        billno=rs.getString("description");
                        company_name = rs.getString("company_name");
                        contact_emails = rs.getString("contact_emails");
                        paymentorder=rs.getString("qwipiPaymentOrderNumber");
                        //toid = rs.getString("toid");
                        authamount = rs.getString("amount");
                        captureamount = rs.getString("amount");
                        accountId = rs.getString("accountid");
                        cardholdername = rs.getString("name");
                        String transid= rs.getString("transid");
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                        icicimerchantid = account.getMerchantId();
                        currency=account.getCurrency();
                        String binUpdateQuery = "update bin_details set isFraud=?, isRefund=? where icicitransid=?";
                        PreparedStatement pPStmt = conn.prepareStatement(binUpdateQuery);
                        if(fraud!=null && fraud.equals("Y"))
                        {
                            occure="Refund BY ADMIN (FRAUD Transaction)"; //set in VO object
                            description = "Refund of " + transid +"  (Fraudulent Transaction)";
                            pPStmt.setString(1,"Y");
                        }
                        if (fraud!=null && fraud.equals("N") )
                        {
                            occure="Refund BY ADMIN";
                            description = "Refund of " + transid ;
                            pPStmt.setString(1,"N");
                        }
                        pPStmt.setString(2,"Y");
                        pPStmt.setString(3,icicitransid);
                        pPStmt.executeUpdate();

                        if (Double.parseDouble(refundamount) > Double.parseDouble(captureamount))
                        {
                            logger.debug("Refund Amount " + refundamount + " is greater than capture amount " + captureamount);
                            sErrorMessage.append("Refund Amount " + refundamount + " is greater than capture amount transaction id is"+icicitransid);
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                        Hashtable qwipiKsnFlag=transaction.getMidKeyForQwipi(accountId);
                        transRespDetails.setRemark(occure);
                        //add charges and change status to markforreverse
                        transactionEntry.newGenericRefundTransaction(icicitransid,rfamt,accountId,description,transRespDetails,auditTrailVO);

                        // set perameter to call gateway
                        TransDetail.setOperation("02");
                        TransDetail.setPaymentOrderNo(paymentorder);
                        TransDetail.setBillNo(billno);
                        TransDetail.setAmount(captureamount);
                        TransDetail.setRefundAmount(refundamount);
                        String md5Info=null;
                        md5Info = Functions.convertmd5(paymentorder+description+captureamount+refundamount+qwipiKsnFlag.get("midkey"));
                        AddressDetail.setMd5info(md5Info);
                        //Now Reverse transaction on the gateway
                        auditTrailVO.setActionExecutorId("toid");
                        auditTrailVO.setActionExecutorName("Admin");

                        AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                        RequestDetail = new QwipiRequestVO(cardDetail,AddressDetail,TransDetail);
                        try
                        {
                            RequestDetail.setKsnUrlFlag((String)qwipiKsnFlag.get("isksnurl"));
                            transRespDetails = (QwipiResponseVO) paymentGateway.processRefund(icicitransid, RequestDetail);


                            if (transRespDetails != null && (transRespDetails.getResultCode()).equals("0"))
                            {
                                Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                                StringBuffer sb = new StringBuffer();
                                sb.append("update transaction_qwipi set status='reversed'");
                                sb.append(",refundamount='"+ ESAPI.encoder().encodeForSQL(MY,transRespDetails.getRefundAmount())+"'");
                                sb.append(",refundcode='"+ESAPI.encoder().encodeForSQL(MY,transRespDetails.getResultCode())+"'");

                                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,icicitransid)+ " and status = 'markedforreversal'");

                                int rows = Database.executeUpdate(sb.toString(), conn);
                                if(fraud!=null && fraud.equals("Y"))
                                {
                                    statusSyncDAO.updateAllRefundTransactionFlowFlag(icicitransid,"fraud",conn);
                                }
                                if(fraud!=null && fraud.equals("N"))
                                {
                                    statusSyncDAO.updateAllRefundTransactionFlowFlag(icicitransid,"reversed",conn);
                                }
                                logger.debug("No of Rows updated : " + rows + "<br>");
                                mailStatus="successful";
                                if (rows == 1)
                                {
                                    refunded = true;

                                }
                                successlist.add(icicitransid);
                                // preparing collections of refunds as per merchant
                                refundDetails = new Hashtable();
                                refundDetails.put("icicitransid",icicitransid);
                                refundDetails.put("captureamount",captureamount);
                                refundDetails.put("refundamount",transRespDetails.getRefundAmount());
                                refundDetails.put("description",transRespDetails.getPaymentOrderNo());
                                refundDetails.put("accountid",accountId);
                                refundDetails.put("cardholdername",cardholdername);

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
                                ActionEntry entry = new ActionEntry();
                                int actionEntry = entry.actionEntryForQwipi(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,transRespDetails,auditTrailVO);
                                entry.closeConnection();
                            }
                            else
                            {
                                sErrorMessage.append("<BR>Error while Process refund Transaction. TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                                failList.add(icicitransid+"<BR>");
                                continue;
                            }
                        }
                        catch (PZTechnicalViolationException tve)
                        {
                            logger.error("Error while Posting data to the bank. refund fail :" ,tve);
                            String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
                            if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()))
                            {
                                sErrorMessage.append("Bank Connectivity Issue while processing your request.TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            }
                            else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
                            {
                                sErrorMessage.append("Your transaction is Timed out.Please check at bank side.TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            }
                            else
                            {
                                sErrorMessage.append("<BR>SystemError while Process refund Transaction. TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            }
                            PZExceptionHandler.handleTechicalCVEException(tve,toid,PZOperations.ADMIN_REFUND);
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }
                        catch (PZConstraintViolationException e)
                        {
                            logger.error("PZConstraintViolation Exception while reversal :",e);
                            PZExceptionHandler.handleCVEException(e,toid,PZOperations.ADMIN_REFUND);
                            failList.add(icicitransid+"<BR>");
                        }
                        /*}
                        catch (NumberFormatException se)
                        {
                            logger.error("Error while Posting data to the bank. refund fail :" ,se);
                            sErrorMessage.append("<BR>SystemError while Process refund Transaction. TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                            failList.add(icicitransid+"<BR>");
                            continue;
                        }*/
                    }
                }
                catch (SystemError se)
                {
                    logger.error("Error while reversal :" ,se);
                    sErrorMessage.append("<BR>Transaction is not found while Process refund TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                    PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(), null,se.getMessage(), se.getCause(), toid, PZOperations.ADMIN_REFUND);
                    failList.add(icicitransid+"<BR>");
                    continue;
                }
                catch (SQLException e)
                {
                    logger.error("Error while reversal :",e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", "SQL Exception Thrown:::", null,e.getMessage(), e.getCause(), null, PZOperations.ADMIN_REFUND);
                    failList.add(icicitransid+"<BR>");
                    continue;
                }//try catch ends
                catch (PZDBViolationException e)
                {
                    logger.error("Error while reversal :",e);
                    PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", "SQL Exception Thrown:::",null, e.getMessage(), e.getCause(), null, PZOperations.ADMIN_REFUND);
                    failList.add(icicitransid+"<BR>");
                    continue;
                }

                catch (NoSuchAlgorithmException e)
                {
                    logger.error("Error while reversal :" ,e);
                    sErrorMessage.append("<BR>Transaction is not found while Process refund TrackingId:<font color=red>"+icicitransid+"</font><BR>");
                    PZExceptionHandler.raiseAndHandleGenericViolationException("QwipiRefundTransaction.java", "doPost()", null, "admin", sErrorMessage.toString(),null, e.getMessage(), e.getCause(), toid, PZOperations.ADMIN_REFUND);
                    failList.add(icicitransid+"<BR>");
                    continue;
                }
                finally
                {
                    Database.closeConnection(conn);
                }
            }

            SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
            sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION, icicitransid, mailStatus,description,null);
            AsynchronousSmsService smsService=new AsynchronousSmsService();
            smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION, icicitransid, mailStatus,description,null);

        }

        transactionEntry.closeConnection();

        sErrorMessage.append("Fail reversed transaction list:   <BR>" + Functions.commaseperatedString(Util.getStringArrayFromVector(failList)));

        sSuccessMessage.append("<br>Successful reversed transaction list:   "+Functions.commaseperatedString(Util.getStringArrayFromVector(successlist))+"<br> ");

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        /*chargeBackMessage.append("");*/
        req.setAttribute("sSuccessMessage",sSuccessMessage);
        req.setAttribute("sErrorMessage",sErrorMessage);
        chargeBackMessage.append(sErrorMessage.toString());

        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiRefundList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}