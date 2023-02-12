import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.ReconDao;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: NISHANT
 * Date: 5/14/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonReconciliationProcess extends HttpServlet
{
    private static Logger log = new Logger(CommonReconciliationProcess.class.getName());
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
        {   log.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;

        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        //variable list
        String mailStatus="";
        String transactionmail = "false";
        String RefundMail ="false";
        String PayoutMail="false";
        String Chargebackmail="false";
        String[] icicitransidStr =null;
        String status=null;
        String paymentordernumber=null;
        String trackingid=null;
        String remark=null;
        String amount=null;
        String chargebackamount=null;
        String reqChargebackAmount=null;
        String desc=null;
        String refundamount=null;
        String payoutAmount=null;
        String captureamount="";
        String dbpaymentnumber=null;
        Collection<Hashtable> listOfRefunds = null;
        String tredtime="";
        String fraudTrans=null;
        String reversedAmount=null;
        String currentRemark=null;
        String currentStatus=null;
        String prestatus="";
        String accountId=null;
        String notificationUrl="";
        String fromtype="";
        String ipAddress=req.getRemoteAddr();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        tredtime=dateFormat.format(calendar.getTime()) ;
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        String actionExId =null;
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StringBuilder chargeBackMessage = new StringBuilder();
        //actionExId= (String) session.getAttribute("merchantid");
        String actionExname="";
        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        for (String icicitransid : icicitransidStr)
        {
            //validate parameters
            try
            {
                trackingid = ESAPI.validator().getValidInput("trackingid",icicitransid,"SafeString",30,false);
                amount = ESAPI.validator().getValidInput("amount",req.getParameter("amount_"+icicitransid),"SafeString",20,true);
                chargebackamount = ESAPI.validator().getValidInput("chargebackamount",req.getParameter("chargebackamount_"+icicitransid),"SafeString",20,true);
                paymentordernumber = ESAPI.validator().getValidInput("paymentnumber",req.getParameter("paymentorderno_" + icicitransid),"SafeString",200,true);
                remark = ESAPI.validator().getValidInput("remark",req.getParameter("remark_" + icicitransid),"SafeString",100,true);
                status = ESAPI.validator().getValidInput("status",req.getParameter("status_"+icicitransid),"SafeString",20,false);
                desc = ESAPI.validator().getValidInput("description",req.getParameter("description_"+icicitransid),"SafeString",80,false);
                dbpaymentnumber=ESAPI.validator().getValidInput("dbpaymentnumber",req.getParameter("dbpaymentnumber_"+icicitransid),"SafeString",100,true);
                refundamount = ESAPI.validator().getValidInput("refundamount",req.getParameter("refundamount_"+icicitransid),"SafeString",20,true);
                captureamount = ESAPI.validator().getValidInput("captureamount",req.getParameter("captureamount_"+icicitransid),"SafeString",20,true);
                if(functions.isValueNull(req.getParameter("chargebackAmount_"+icicitransid)))
                    reqChargebackAmount = ESAPI.validator().getValidInput("chargebackAmount",req.getParameter("chargebackAmount_"+icicitransid),"SafeString",20,true);
                payoutAmount = ESAPI.validator().getValidInput("payoutamount",req.getParameter("payoutamount_"+icicitransid),"SafeString",20,true);
                accountId = ESAPI.validator().getValidInput("accountid",req.getParameter("accountid_"+icicitransid),"SafeString",30,false);
                fraudTrans = ESAPI.validator().getValidInput("isFraud",req.getParameter("isFraud_" + icicitransid),"SafeString",30,true);
                reversedAmount = ESAPI.validator().getValidInput("reversedAmount",req.getParameter("reversedAmount_"+icicitransid),"SafeString",30,true);
                currentRemark = ESAPI.validator().getValidInput("currentRemark",req.getParameter("currentRemark_"+icicitransid),"SafeString",30,true);
                currentStatus = ESAPI.validator().getValidInput("currentStatus",req.getParameter("currentStatus_"+icicitransid),"SafeString",30,true);
                //captureAmount1 = ESAPI.validator().getValidInput("reversedAmount",req.getParameter("reversedAmount_"+icicitransid),"SafeString",30,true);
                notificationUrl = req.getParameter("notificationUrl_" + icicitransid);
                actionExId = req.getParameter("toid_"+icicitransid);  // Admin user id
                prestatus=req.getParameter("prestatus_" + icicitransid);
                fromtype=req.getParameter("fromtype_" + icicitransid);

            }
            catch (ValidationException e)
            {
                log.error("validation Exception",e);
                sErrorMessage.append("Invalid Input Parameter for Trackingid:- "+icicitransid+"<BR>");
                continue;
            }

            if(req.getParameter("amount_"+icicitransid)!= null)
            {
                amount = req.getParameter("amount_" + icicitransid);
            }
            else
            {
                sErrorMessage.append("Invalid Amount for Trackingid:- "+icicitransid+"<BR>");
            }
            double chargebackAmount=0.00;
            if(functions.isValueNull(chargebackamount) && functions.isValueNull(reqChargebackAmount))
            {
                chargebackAmount= Double.parseDouble(chargebackamount) - Double.parseDouble(reqChargebackAmount);
            }

            if(status!= null)
            {
                PreparedStatement pstmt=null;

                try
                {
                    auditTrailVO.setActionExecutorId(actionExId);
                    conn=Database.getConnection();
                    StringBuffer updatequery=new StringBuffer();
                    StringBuffer insertquery=new StringBuffer();


                    // currRemark & currstatus from hidden field

                    if(currentStatus.equals("cancelstarted")  && status.equals("authfailed"))
                    {
                        remark = currentRemark;
                    }

                    if("failed".equalsIgnoreCase(status))
                    {
                        updatequery.append("update transaction_common set status='failed' where trackingid=?");
                        pstmt=conn.prepareStatement(updatequery.toString());
                        pstmt.setString(1,icicitransid);
                        int i= pstmt.executeUpdate();
                        if(i==1)
                        {
                            insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?)");
                            pstmt=conn.prepareStatement(insertquery.toString());
                            pstmt.setString(1,icicitransid);
                            pstmt.setString(2,"Validation Failed");
                            pstmt.setString(3,"failed");
                            pstmt.setString(4,tredtime);
                            pstmt.setString(5,amount);
                            pstmt.setString(6,actionExId);
                            pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                            int j= pstmt.executeUpdate();
                            if(j==1)
                            {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"failed",conn);
                                sSuccessMessage.append("Success  Treatement(Status:- Failed) for trackingid : "+icicitransid+"<BR>");
                            }
                            else
                            {
                                sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                            }
                            remark = "Validation Failed";
                        }
                        else
                        {
                            sErrorMessage.append("ERROR while giving Treatement(Status:- Failed) for trackingid :"+icicitransid+"<BR>");
                        }
                    }
                    else if("authfailed".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,remark=?, status='authfailed', failuretimestamp = ? where description=? and trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,functions.getTimestamp());
                            pstmt.setString(4,desc);
                            pstmt.setString(5,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Authorisation Failed");
                                pstmt.setString(3,"authfailed");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                pstmt.setString(6,remark);
                                pstmt.setString(7,paymentordernumber);
                                pstmt.setString(8,actionExId);
                                pstmt.setString(9,"Admin"+"-"+user.getAccountName());
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "authfailed", conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- authfailed) for trackingid : "+icicitransid+"<BR>");
                                    transactionmail="true";
                                    if(remark!=null && !remark.equals(""))
                                    {
                                        mailStatus = "Transaction Declined ( "+remark+" )";
                                    }else{
                                        mailStatus = "Transaction Declined";
                                    }

                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- authfailed) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }
                    else if("authstarted".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber))
                        {
                            sErrorMessage.append("Enter paymentID for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?, status='authstarted' where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Auth Started");
                                pstmt.setString(3,"authstarted");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                //pstmt.setString(6,paymentordernumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"authstarted",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- Authstarted) for trackingid : " + icicitransid + "<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                                remark = "transaction pending";
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- Authstarted) for trackingid :"+icicitransid+"<BR>");
                            }

                        }

                    }
                    else if("capturesuccess".equalsIgnoreCase(status))
                    {
                        if(captureamount==null || ("").equals(captureamount) || captureamount.equals("0.00")||Double.parseDouble(captureamount)==0.00)
                        {
                            sErrorMessage.append("Capture Amount is empty or 0.00 for trackingid :"+icicitransid+"<BR>");
                        }
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }

                        else
                        {
                            log.error("fromtype----->"+fromtype);
                            if(Double.parseDouble(captureamount)!=0.00 && "jpbt".equalsIgnoreCase(fromtype)){
                                captureamount=String.valueOf(Double.parseDouble(captureamount));
                            }
                            else if(Double.parseDouble(captureamount)!=0.00 && Double.parseDouble(captureamount)<=Double.parseDouble(amount))
                            {
                                captureamount=String.valueOf(Double.parseDouble(captureamount));
                            }
                            else
                            {
                                sErrorMessage.append("Cannot Capture Transaction as Capture Amount is greater than Available Amount or 0.00 or empty for Tracking Id:"+trackingid);
                                chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"#34495e\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Success Result</font></td><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Fail Result</font></td></tr>");
                                chargeBackMessage.append("<tr bgcolor=\"#f5f5f5\" class=\"textb\"><td>");
                                chargeBackMessage.append(sSuccessMessage.toString());
                                chargeBackMessage.append("</td><td>");
                                chargeBackMessage.append(sErrorMessage.toString());
                                chargeBackMessage.append("</td></tr></table>");
                                req.setAttribute("cbmessage", chargeBackMessage.toString());
                                RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
                                rd.forward(req, res);
                                return;

                            }
                            updatequery.append("update transaction_common set status='capturesuccess', ipaddress=?, captureamount=?, paymentid=?, timestamp=?, remark='Payment Success', successtimestamp = ? where description=? and trackingid=?  ");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,ipAddress);
                            pstmt.setString(2,captureamount);
                            pstmt.setString(3,paymentordernumber);
                            pstmt.setString(4,tredtime);
                            pstmt.setString(5,functions.getTimestamp());
                            pstmt.setString(6,desc);
                            pstmt.setString(7,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {   //add billing discriptor field
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,amount,responsetime,responsetransactionid,responsetransactionstatus,responsedescriptor,actionexecutorid,actionexecutorname,remark) VALUES (?,'Capture Successful','capturesuccess',?,?,?,'Payment Success',?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,captureamount);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,paymentordernumber);
                                pstmt.setString(5,GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString());
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "capturesuccess", conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- capturesuccess) for trackingid : " + icicitransid + "<BR>");
                                    if(prestatus.equals("markedforreversal")){
                                        transactionmail="false";
                                    }else
                                    {
                                        transactionmail = "true";
                                    }
                                    if(remark!=null && !remark.equals(""))
                                    {
                                        mailStatus="Successful ("+remark+")";
                                    }
                                    else
                                    {
                                        mailStatus="Successful";
                                    }
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- capturesuccess) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }
                    else if("chargebackreversed".equalsIgnoreCase(status))
                    {

                        if(reqChargebackAmount==null || ("").equals(reqChargebackAmount) || reqChargebackAmount.equals("0.00"))
                        {
                            sErrorMessage.append("Chargeback Amount is empty or 0.00 for trackingid :"+icicitransid+"<BR>");
                        }
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }

                        else
                        {
                            if(Double.parseDouble(reqChargebackAmount)!=0.00 && Double.parseDouble(chargebackamount)>=Double.parseDouble(reqChargebackAmount))
                            {
                                reqChargebackAmount=String.valueOf(Double.parseDouble(reqChargebackAmount));
                            }
                            else
                            {
                                sErrorMessage.append("Cannot reverse Chargeback Transaction as Requested CBK reverse Amount is greater than Total Chargeback Amount or 0.00 for Tracking Id "+trackingid);
                                chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"#34495e\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Success Result</font></td><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Fail Result</font></td></tr>");
                                chargeBackMessage.append("<tr bgcolor=\"#f5f5f5\" class=\"textb\"><td>");
                                chargeBackMessage.append(sSuccessMessage.toString());
                                chargeBackMessage.append("</td><td>");
                                chargeBackMessage.append(sErrorMessage.toString());
                                chargeBackMessage.append("</td></tr></table>");
                                req.setAttribute("cbmessage", chargeBackMessage.toString());
                                RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
                                rd.forward(req, res);
                                return;

                            }

                            updatequery.append("update transaction_common set status='chargebackreversed', ipaddress=?, chargebackamount=?, paymentid=?, timestamp=?, remark='Chargeback Reversed' where description=? and trackingid=?  ");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,ipAddress);
                            pstmt.setDouble(2,chargebackAmount);
                            pstmt.setString(3,paymentordernumber);
                            pstmt.setString(4,tredtime);
                            pstmt.setString(5,desc);
                            pstmt.setString(6,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {   //add billing discriptor field
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,amount,responsetime,responsetransactionid,responsetransactionstatus,responsedescription,actionexecutorid,actionexecutorname,remark) VALUES (?,'Chargeback Reversed','chargebackreversed',?,?,?,'Chargeback Reversed',?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,reqChargebackAmount);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,paymentordernumber);
                                pstmt.setString(5,"Reversal of Chargeback");
                                pstmt.setString(6,"0");
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"chargebackreversed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- chargebackreversed) for trackingid : "+icicitransid+"<BR>");
                                    Chargebackmail="true";
                                    mailStatus="ChargebackReversed";
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- chargebackreversed) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }
                    else if("casefiling".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        if(Double.parseDouble(chargebackamount)!=Double.parseDouble(amount))
                        {
                            sErrorMessage.append("Please perform a FULL chargeback before marking the transaction as 'Casefiling'"+icicitransid+"<BR>");
                        }
                        else
                        {
                            ReconDao reconDao = new ReconDao();
                            boolean isRecordFound = reconDao.getCaseFilingRecord(icicitransid, status);
                            if (isRecordFound == true)
                            {
                                sErrorMessage.append("Transaction already marked as casefiling" + icicitransid + "<BR>");
                            }
                            else
                            {
                                updatequery.append("update transaction_common set ipaddress=?, chargebackamount=?, paymentid=?, timestamp=?, remark='Case Filing' where description=? and trackingid=?  ");
                                pstmt = conn.prepareStatement(updatequery.toString());
                                pstmt = conn.prepareStatement(updatequery.toString());
                                pstmt.setString(1, ipAddress);
                                pstmt.setDouble(2, Double.parseDouble(chargebackamount));
                                pstmt.setString(3, paymentordernumber);
                                pstmt.setString(4, tredtime);
                                pstmt.setString(5, desc);
                                pstmt.setString(6, icicitransid);
                                int i = pstmt.executeUpdate();
                                if (i == 1)
                                {   //add billing discriptor field
                                    insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,amount,responsetime,responsetransactionid,responsetransactionstatus,responsedescription,actionexecutorid,actionexecutorname,remark) VALUES (?,'Case Filing','casefiling',?,?,?,'Case Filing',?,?,?,?)");
                                    pstmt = conn.prepareStatement(insertquery.toString());
                                    pstmt.setString(1, icicitransid);
                                    pstmt.setString(2, chargebackamount);
                                    pstmt.setString(3, tredtime);
                                    pstmt.setString(4, paymentordernumber);
                                    pstmt.setString(5, "Case Filing");
                                    pstmt.setString(6, "0");
                                    pstmt.setString(7, "Admin" + "-" + user.getAccountName());
                                    pstmt.setString(8, remark);
                                    int j = pstmt.executeUpdate();
                                    if (j == 1)
                                    {
                                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, "casefiling", conn);
                                        sSuccessMessage.append("Success  Treatement(Status:- casefiling) for trackingid : " + icicitransid + "<BR>");
                                        Chargebackmail="true";
                                        mailStatus="Casefiling";
                                    }
                                    else
                                    {
                                        sErrorMessage.append("error while insert in detail table. Trackingid :" + trackingid + "<BR>");
                                    }
                                }
                                else
                                {
                                    sErrorMessage.append("ERROR while giving Treatement(Status:- casefiling) for trackingid :" + icicitransid + "<BR>");
                                }
                            }
                        }
                    }

                    else if("payoutcancelfailed".equalsIgnoreCase(status))
                    {

                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID for trackingid :"+icicitransid+"<BR>");
                        }

                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,remark=?,status='payoutcancelfailed' where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Payout Cancel Failed");
                                pstmt.setString(3,"payoutcancelfailed");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                //pstmt.setString(6,paymentordernumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"payoutcancelfailed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- payoutcancelfailed) for trackingid : "+icicitransid+"<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                                remark = "transaction pending";
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- payoutcancelfailed) for trackingid :"+icicitransid+"<BR>");
                            }

                        }

                    }

                    else if("payoutsuccessful".equalsIgnoreCase(status))
                    {
                        if(payoutAmount==null || ("").equals(payoutAmount))
                        {
                            sErrorMessage.append(" Payout Amount is empty for trackingid :"+icicitransid+"<BR>");
                        }
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID & Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,payoutamount=?,remark=?, status='payoutsuccessful', payouttimestamp=? where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,payoutAmount);
                            pstmt.setString(3,remark);
                            pstmt.setString(4,functions.getTimestamp());
                            pstmt.setString(5,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Payout Successful");
                                pstmt.setString(3,"payoutsuccessful");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,payoutAmount);
                                //pstmt.setString(6,paymentordernumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"payoutsuccessful",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- payoutsuccessful) for trackingid : "+icicitransid+"<BR>");
                                    PayoutMail="true";
                                    mailStatus="SUCCESS";
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                                remark = "transaction pending";
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- payoutsuccessful) for trackingid :"+icicitransid+"<BR>");
                            }

                        }

                    }

                    else if("payoutfailed".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber)&& remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter PaymentID & Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,remark=?, status='payoutfailed' where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Payout Failed");
                                pstmt.setString(3,"payoutfailed");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                //pstmt.setString(6,paymentordernumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"payoutfailed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- payoutfailed) for trackingid : "+icicitransid+"<BR>");
                                    PayoutMail="true";
                                    mailStatus="FAILED";
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                                remark = "transaction pending";
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- payoutfailed) for trackingid :"+icicitransid+"<BR>");
                            }

                        }

                    }

                    else if("authcancelled".equalsIgnoreCase(status))
                    {
                        if(remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,remark=?, status='authcancelled' where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Authorisation cancelled");
                                pstmt.setString(3,"authcancelled");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                //pstmt.setString(6,paymentordernumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"authcancelled",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- authcancelled) for trackingid : "+icicitransid+"<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                                remark = "transaction pending";
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- authcancelled) for trackingid :"+icicitransid+"<BR>");
                            }

                        }

                    }

                    else if("authsuccessful".equalsIgnoreCase(status))
                    {

                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null || ("").equals(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }

                        else
                        {
                            updatequery.append("update transaction_common set status='authsuccessful', ipaddress=?, amount=?, paymentid=?, timestamp=?,remark=? where description=?, successtimestamp=? and trackingid=?  ");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1, ipAddress);
                            pstmt.setString(2,amount);
                            pstmt.setString(3,paymentordernumber);
                            pstmt.setString(4,tredtime);
                            pstmt.setString(5,remark);
                            pstmt.setString(6,desc);
                            pstmt.setString(7, functions.getTimestamp());
                            pstmt.setString(8,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {   //add billing discriptor field
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,amount,responsetime,responsetransactionid,responsetransactionstatus,responsedescriptor,actionexecutorid,actionexecutorname,remark) VALUES (?,'Auth Successful','authsuccessful',?,?,?,?,'Transaction  successful',?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,amount);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,paymentordernumber);
                                pstmt.setString(5,GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString());
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                pstmt.setString(8,remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"authsuccessful",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- authsuccessful) for trackingid : " + icicitransid + "<BR>");
                                    transactionmail="true";
                                    if(remark!=null && !remark.equals(""))
                                    {
                                        mailStatus="Successful ("+remark+")";
                                    }
                                    else
                                    {
                                        mailStatus="Successful";
                                    }
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- authsuccessful) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }

                    else if("settled".equalsIgnoreCase(status))
                    {
                        if(remark==null || ("").equals(remark) )
                        {
                            sErrorMessage.append("Enter remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set status='settled',remark=? where description=? and trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            //pstmt.setString(1,paymentordernumber);
                            pstmt.setString(1,remark);
                            pstmt.setString(2,desc);
                            pstmt.setString(3,icicitransid);
                            int i=pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1, icicitransid);
                                pstmt.setString(2,"Settled");
                                pstmt.setString(3,"settled");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                pstmt.setString(6,remark);
                                pstmt.setString(7,paymentordernumber);
                                pstmt.setString(8,actionExId);
                                pstmt.setString(9,"Admin"+"-"+user.getAccountName());
                                int j=pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"settled",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- settled) for trackingid : "+icicitransid+"<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("Error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- settled) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }

                    else if("capturefailed".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber) && remark==null ||("").equalsIgnoreCase(remark))
                        {
                            sErrorMessage.append("Enter paymentID for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery.append("update transaction_common set paymentid=?,remark=?, status='capturefailed' where description=? and trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,desc);
                            pstmt.setString(4,icicitransid);
                            int i=pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1, icicitransid);
                                pstmt.setString(2,"Capture Failed");
                                pstmt.setString(3,"capturefailed");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                pstmt.setString(6, remark);
                                pstmt.setString(7, paymentordernumber);
                                pstmt.setString(8, actionExId);
                                pstmt.setString(9,"Admin"+"-"+user.getAccountName());
                                int j=pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"capturefailed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- capturefailed) for trackingid : "+icicitransid+"<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("Error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- capturefailed) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }


                    else if("markedforreversal".equalsIgnoreCase(status))
                    {
                        if(refundamount==null || ("").equals(refundamount))
                        {
                            sErrorMessage.append("PaymentID or Refund Amount is empty for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {

                            String totalrefundamount="";
                            if(functions.isValueNull(refundamount) && Double.parseDouble(refundamount)!= 0.00 && Double.parseDouble(refundamount) <= Double.parseDouble(captureamount))
                            {

                                totalrefundamount=String.valueOf(Double.parseDouble(refundamount) + Double.parseDouble(reversedAmount));

                            }
                            else
                            {
                                sErrorMessage.append("Cannot Reversal Request Sent Transaction as Refund Amount is greater than Available Amount or 0.00 for Tracking Id "+trackingid);

                                chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"#34495e\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Success Result</font></td><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Fail Result</font></td></tr>");
                                chargeBackMessage.append("<tr bgcolor=\"#f5f5f5\" class=\"textb\"><td>");
                                chargeBackMessage.append(sSuccessMessage.toString());
                                chargeBackMessage.append("</td><td>");
                                chargeBackMessage.append(sErrorMessage.toString());
                                chargeBackMessage.append("</td></tr></table>");
                                req.setAttribute("cbmessage", chargeBackMessage.toString());
                                RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
                                rd.forward(req, res);
                                return;

                            }
                            updatequery.append("update transaction_common set paymentid=?,remark=?,refundamount=?, status='markedforreversal' where description=? and trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2, remark);
                            pstmt.setString(3,reversedAmount);
                            pstmt.setString(4,desc);
                            pstmt.setString(5,icicitransid);
                            int i=pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details (trackingid,ACTION,STATUS,responsetime,amount,remark,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1, icicitransid);
                                pstmt.setString(2,"Reversal Request Sent");
                                pstmt.setString(3,"markedforreversal");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,refundamount);
                                pstmt.setString(6, remark);
                                pstmt.setString(7, paymentordernumber);
                                pstmt.setString(8, actionExId);
                                pstmt.setString(9,"Admin"+"-"+user.getAccountName());
                                int j=pstmt.executeUpdate();
                                if(j==1)
                                {
                                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"markedforreversal",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- markedforreversal) for trackingid : "+icicitransid+"<BR>");
                                }
                                else
                                {
                                    sErrorMessage.append("Error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- markedforreversal) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }
                    else if("reversed".equalsIgnoreCase(status))
                    {
                        if(!functions.isValueNull(refundamount) || "0.00".equals(refundamount) || "0.0".equals(refundamount))
                        {
                            sErrorMessage.append("Refund Amount is empty or 0.00 for trackingid :"+icicitransid+"<BR>");
                        }else if(!functions.isValueNull(paymentordernumber) && !functions.isValueNull(remark))
                        {
                            sErrorMessage.append("Enter paymentID and Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {

                            String totalrefundamount="";
                            if(functions.isValueNull(refundamount)  && functions.isValueNull(reversedAmount) && functions.isValueNull(captureamount) && Double.parseDouble(refundamount)<=Double.parseDouble(captureamount)-Double.parseDouble(reversedAmount))
                            {

                                totalrefundamount=String.valueOf(Double.parseDouble(refundamount) + Double.parseDouble(reversedAmount));

                            }
                            else
                            {
                                sErrorMessage.append("Cannot Refund Transaction as Refund Amount is greater than Available Amount or 0.00 for Tracking Id "+trackingid);

                                chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"#34495e\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Success Result</font></td><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Fail Result</font></td></tr>");
                                chargeBackMessage.append("<tr bgcolor=\"#f5f5f5\" class=\"textb\"><td>");
                                chargeBackMessage.append(sSuccessMessage.toString());
                                chargeBackMessage.append("</td><td>");
                                chargeBackMessage.append(sErrorMessage.toString());
                                chargeBackMessage.append("</td></tr></table>");
                                req.setAttribute("cbmessage", chargeBackMessage.toString());
                                RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
                                rd.forward(req, res);
                                return;

                            }



                            String action="";
                            String dbstatus="";
                            if(Double.parseDouble(totalrefundamount)< Double.parseDouble(captureamount))
                            {
                                // is fraud transa check box
                                if (fraudTrans != null && fraudTrans.equalsIgnoreCase("Y"))
                                {
                                    action = "Partially Reversed (Fraud)";
                                    remark = "Partially Successful Fraud";
                                    dbstatus = "partialrefund";
                                }
                                else
                                {
                                    action = ActionEntry.ACTION_PARTIAL_REFUND;
                                    remark="Partial Refund Successful";
                                    dbstatus = ActionEntry.STATUS_PARTIAL_REFUND;

                                }

                            }
                            else
                            {
                                if (fraudTrans != null && fraudTrans.equalsIgnoreCase("Y"))
                                {
                                    action = ActionEntry.ACTION_REVERSAL_SUCCESSFUL_FRAUD;
                                    remark = "Reversal Successful Fraud";
                                    dbstatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL_FRAUD;

                                }
                                else
                                {
                                    action = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                    remark="Reversal Successful";
                                    dbstatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;

                                }
                            }
                            updatequery.append("update transaction_common set status='reversed',refundinfo='Admin Treatment by Reconciliation',refundamount=?,paymentid=?,remark=?, refundtimestamp=? where trackingid=?");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,totalrefundamount);
                            pstmt.setString(2,paymentordernumber);
                            pstmt.setString(3, remark);
                            pstmt.setString(4, functions.getTimestamp());
                            pstmt.setString(5, icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details(trackingid,amount,ACTION,STATUS,responsetime,responsetransactionid,actionexecutorid,actionexecutorname,remark) VALUES (?,?,?,?,?,?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1, icicitransid);
                                pstmt.setString(2,refundamount);
                                pstmt.setString(3,action);
                                pstmt.setString(4,dbstatus);
                                pstmt.setString(5,tredtime);
                                pstmt.setString(6,dbpaymentnumber);
                                pstmt.setString(7,actionExId);
                                pstmt.setString(8, "Admin"+"-"+user.getAccountName());
                                pstmt.setString(9, remark);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    if(fraudTrans.equalsIgnoreCase("Y"))
                                    {
                                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"fraud",conn);
                                        sSuccessMessage.append("Success  Treatement(Status:- Fraud reversed) for trackingid : "+icicitransid+"<BR>");
                                    }
                                    else
                                    {
                                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"reversed",conn);
                                        sSuccessMessage.append("Success  Treatement(Status:- reversed) for trackingid : "+icicitransid+"<BR>");
                                    }
                                    RefundMail="true";
                                    mailStatus="successful";
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                log.debug("inside else ");
                                log.debug("inside else i:::"+i);
                                sErrorMessage.append("ERROR while giving Treatement(Status:- reversed) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }

                    else if("partialrefund".equalsIgnoreCase(status))
                    {

                        /*if(refundChecker.isRefundAllowed(trackingid))
                        {
                            refundResponse = paymentProcess.refund(refundRequest);
                            responseStatus = refundResponse.getStatus();
                            if (PZResponseStatus.ERROR.equals(responseStatus))
                            {
                                responseMessage = "Refund Operation encountered internal error while reversing for Tracking ID: "+trackingId;
                                responseMessage = "Refund Amount is greater than capture amount:"+captureAmount;
                            }
                            else if (PZResponseStatus.FAILED.equals(responseStatus))
                            {
                                responseMessage="Refund Operation is failed for Tracking ID: "+trackingId;
                            }
                            else if(PZResponseStatus.SUCCESS.equals(responseStatus))
                            {
                                mailStatus="successful";
                                responseMessage="Refund Operation is successful for Tracking ID: "+trackingId;
                            }
                            else if(PZResponseStatus.PENDING.equals(responseStatus))
                            {
                                responseMessage = refundResponse.getResponseDesceiption();
                                mailStatus = responseMessage;
                            }
                        }*/
                        if(!functions.isValueNull(refundamount))
                        {
                            sErrorMessage.append("PaymentID or Refund Amount is empty for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            String action=" ";
                            // is fraud transa check box
                            if(fraudTrans!=null && fraudTrans.equalsIgnoreCase("Y"))
                            {
                                action="Reversal Successful Fraud";
                                remark="Reversal Successful Fraud";
                            }
                            else
                            {
                                action="Reversal Successful";
                                remark="Reversal Successful";
                            }
                            updatequery.append("update transaction_common set status='partialrefund',refundinfo='Admin Treatment by Reconciliation',refundamount=?,paymentid=?, refundtimestamp=? where trackingid=? and status='capturesuccess'");
                            pstmt=conn.prepareStatement(updatequery.toString());
                            pstmt.setString(1,refundamount);
                            pstmt.setString(2,paymentordernumber);
                            pstmt.setString(3,functions.getTimestamp());
                            pstmt.setString(4,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery.append("INSERT INTO transaction_common_details(trackingid,amount,ACTION,STATUS,responsetime,responsetransactionid,actionexecutorid,actionexecutorname) VALUES (?,?,?,'partialrefund',?,?,?,?)");
                                pstmt=conn.prepareStatement(insertquery.toString());
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,refundamount);
                                pstmt.setString(3,action);
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,dbpaymentnumber);
                                pstmt.setString(6,actionExId);
                                pstmt.setString(7,"Admin"+"-"+user.getAccountName());
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {
                                    if(fraudTrans.equalsIgnoreCase("Y"))
                                    {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"fraud",conn);
                                        sSuccessMessage.append("Success  Treatement(Status:- Fraud reversed) for trackingid : "+icicitransid+"<BR>");
                                    }
                                    else
                                    {
                                        statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"reversed",conn);
                                        sSuccessMessage.append("Success  Treatement(Status:- partial refund) for trackingid : "+icicitransid+"<BR>");
                                    }
                                }
                                else
                                {
                                    sErrorMessage.append("error while insert in detail table. Trackingid :"+trackingid+"<BR>");
                                }
                            }
                            else
                            {
                                sErrorMessage.append("ERROR while giving Treatement(Status:- reversed) for trackingid :"+icicitransid+"<BR>");
                            }
                        }
                    }
                    else
                    {
                        sErrorMessage.append("Undefind Status treatement to process transaction Trackingid:- "+icicitransid+"<BR>");
                    }


                    //Sending Notification on NotificationURL
                    log.error("Notificatin Sending to---"+notificationUrl+"---"+icicitransid);
                    MerchantDAO merchantDAO = new MerchantDAO();
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(actionExId);
                    log.error("ReconciliationNotification flag for ---"+actionExId+"---"+merchantDetailsVO.getReconciliationNotification());
                    if(functions.isValueNull(notificationUrl) && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                    {
                        log.error("inside sending notification---"+notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(icicitransid);
                        log.error("status----->"+status);
                        if("capturesuccess".equalsIgnoreCase(status))
                        {
                            log.error("transactionDetailsVO.getCaptureAmount()--->"+transactionDetailsVO.getCaptureAmount());
                            transactionDetailsVO.setTemplateamount(transactionDetailsVO.getCaptureAmount());
                        }
                        log.error("tmpl_amount----->"+transactionDetailsVO.getTemplateamount());
                        transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                        if(functions.isValueNull(transactionDetailsVO.getCcnum()) && functions.isValueNull(transactionDetailsVO.getExpdate()))
                        {
                            transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                            transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                        }
                        transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString());
                        asyncNotificationService.sendNotification(transactionDetailsVO,icicitransid,status,remark);
                    }
                    //Send Mail
                    String mailForEvent="";
                    if("Y".equals(merchantDetailsVO.getSuccessReconMail()) && transactionmail.equals("true"))
                    {
                        mailForEvent="transactionMail";
                        sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, icicitransid, mailStatus, remark, GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString(),mailForEvent);
                    }
                    if("Y".equals(merchantDetailsVO.getRefundReconMail()) && RefundMail.equals("true"))
                    {
                        mailForEvent="refundMail";
                        mailStatus="Refund Successful";
                        sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, icicitransid, mailStatus, remark, null,mailForEvent);
                    }
                    if("Y".equals(merchantDetailsVO.getPayoutReconMail()) && PayoutMail.equals("true")){
                        mailForEvent="payoutMail";
                        mailStatus="Payout Successful";
                        sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, icicitransid, mailStatus, remark, null,mailForEvent);
                    }
                    if("Y".equals(merchantDetailsVO.getChargebackReconMail()) && Chargebackmail.equals("true")){
                        mailForEvent="chargebackMail";
                        sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, icicitransid, mailStatus, remark, null,mailForEvent);
                    }
                }
                catch(Exception e)
                {
                    log.error("EXCEPTION while process transaction",e);
                    sErrorMessage.append("Internal system error while processing transaction");
                }
                finally
                {
                    Database.closePreparedStatement(pstmt);
                    Database.closeConnection(conn);
                }
            }
            else
            {
                sErrorMessage.append("Select Status treatement to process transaction Trackingid:- "+icicitransid+"<BR>");
            }
        }
        chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"#34495e\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Success Result</font></td><td valign=\"middle\" bgcolor=\"#34495e\"><font class=\"texthead\" color=\"#FFFFFF\" size=\"1\" > Fail Result</font></td></tr>");
        chargeBackMessage.append("<tr bgcolor=\"#f5f5f5\" class=\"textb\"><td>");
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("</td><td>");
        chargeBackMessage.append(sErrorMessage.toString());
        chargeBackMessage.append("</td></tr></table>");
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}