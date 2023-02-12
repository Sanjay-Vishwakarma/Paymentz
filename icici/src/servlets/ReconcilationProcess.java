import com.directi.pg.Admin;
import com.directi.pg.*;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 4/15/13
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReconcilationProcess extends HttpServlet
{
    private static Logger log = new Logger(ReconcilationProcess.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("success");


        if (!Admin.isLoggedIn(session))
        {   log.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
        }
        catch (SystemError systemError)
        {
            log.error("Error while reversal :",systemError);

            sErrorMessage.append("Error while connecting to Database.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to QwipiReconcilationList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        //variable list
        String[] icicitransidStr =null;
        String status=null;
        String paymentordernumber=null;
        String trackingid=null;
        String remark=null;
        String amount=null;
        String desc=null;
        String refundamount=null;
        String dbpaymentnumber=null;
        Collection<Hashtable> listOfRefunds = null;
        String tredtime="";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        tredtime=dateFormat.format(calendar.getTime()) ;
        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to QwipiRefundList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();

        for (String icicitransid : icicitransidStr)
        {
            //validate parameters
            try
            {
                trackingid = ESAPI.validator().getValidInput("trackingid",icicitransid,"SafeString",30,true);
                amount = ESAPI.validator().getValidInput("amount",req.getParameter("amount_"+icicitransid),"SafeString",20,true);
                paymentordernumber = ESAPI.validator().getValidInput("paymentnumber",req.getParameter("qwipipaymentorderno_" + icicitransid),"SafeString",30,true);
                remark = ESAPI.validator().getValidInput("remark",req.getParameter("remark_" + icicitransid),"SafeString",100,true);
                status = ESAPI.validator().getValidInput("status",req.getParameter("status_"+icicitransid),"SafeString",20,false);
                desc = ESAPI.validator().getValidInput("description",req.getParameter("description_"+icicitransid),"SafeString",50,false);
                dbpaymentnumber=ESAPI.validator().getValidInput("dbpaymentnumber",req.getParameter("dbpaymentnumber_"+icicitransid),"SafeString",20,true);
                refundamount = ESAPI.validator().getValidInput("refundamount",req.getParameter("refundamount_"+icicitransid),"SafeString",20,true);
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

            if(status!= null)
            {
                PreparedStatement pstmt=null;
                try
                {
                    conn=Database.getConnection();
                    String updatequery=null;
                    String insertquery=null;

                    if("failed".equalsIgnoreCase(status))
                    {
                        updatequery="update transaction_qwipi set status='failed' where trackingid=?";
                        pstmt=conn.prepareStatement(updatequery);
                        pstmt.setString(1,icicitransid);
                        int i= pstmt.executeUpdate();
                        if(i==1)
                        {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"failed",conn);
                            sSuccessMessage.append("Success  Treatement(Status:- Failed) for trackingid : "+icicitransid+"<BR>");
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
                            sErrorMessage.append("Enter QWIPIpaymentorder number, Remark for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery="update transaction_qwipi set qwipiPaymentOrderNumber=?, status='authfailed', remark=? where description=? and trackingid=?";
                            pstmt=conn.prepareStatement(updatequery);
                            pstmt.setString(1,paymentordernumber);
                            pstmt.setString(2,remark);
                            pstmt.setString(3,desc);
                            pstmt.setString(4,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery="insert into transaction_qwipi_details (parentid,action,status,timestamp,amount,operationCode,responseResultCode,responseDateTime,qwipiPaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) values (?,?,?,?,?,?,?,?,?,?,?,?)";
                                pstmt=conn.prepareStatement(insertquery);
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,"Authorisation Failed");
                                pstmt.setString(3,"authfailed");
                                pstmt.setString(4,tredtime);
                                pstmt.setString(5,amount);
                                pstmt.setString(6,"01");
                                pstmt.setString(7,"1");
                                pstmt.setString(8,"");
                                pstmt.setString(9,paymentordernumber);
                                pstmt.setString(10,remark);
                                pstmt.setString(11,"");
                                pstmt.setString(12,"");
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"authfailed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- authfailed) for trackingid : "+icicitransid+"<BR>");
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
                    else if("capturesuccess".equalsIgnoreCase(status))
                    {
                        if(paymentordernumber==null || ("").equals(paymentordernumber))
                        {
                            sErrorMessage.append("Enter QWIPIpaymentorder number for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery="update transaction_qwipi set status='capturesuccess',ipcode='2886732298', ipaddress='172.16.10.10', captureamount=?, hrcode='', fixamount=0.75, taxper=0, mid='-1475589951', qwipiPaymentOrderNumber=?, qwipiTransactionDateTime=?, remark='Payment Success' where description=? and trackingid=?  ";
                            pstmt=conn.prepareStatement(updatequery);
                            pstmt.setString(1,amount);
                            pstmt.setString(2,paymentordernumber);
                            pstmt.setString(3,tredtime);
                            pstmt.setString(4,desc);
                            pstmt.setString(5,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery="insert into transaction_qwipi_details (parentid,action,status,amount,operationCode,responseResultCode,responseDateTime,qwipiPaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) values (?,'Capture Successful','capturesuccess',?,01,1,?,?,'Payment Success',' ',' ')";
                                pstmt=conn.prepareStatement(insertquery);
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,amount);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,paymentordernumber);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"capturesuccess",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- capturesuccess) for trackingid : "+icicitransid+"<BR>");
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
                    else if("reversed".equalsIgnoreCase(status))
                    {
                        if(refundamount==null || ("").equals(refundamount))
                        {
                            sErrorMessage.append("Payment order number or Refund Amount is empty for trackingid :"+icicitransid+"<BR>");
                        }
                        else
                        {
                            updatequery="update transaction_qwipi set status='reversed',refundinfo='Admin Treatment by Reconciliation',refundcode='0',refundamount=? where trackingid=? and status='markedforreversal'";
                            pstmt=conn.prepareStatement(updatequery);
                            pstmt.setString(1,refundamount);
                            pstmt.setString(2,icicitransid);
                            int i= pstmt.executeUpdate();
                            if(i==1)
                            {
                                insertquery="insert into transaction_qwipi_details(parentid,amount,action,status,operationCode,responseResultcode,responseDateTime,qwipiPaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) values (?,?,'Reversal Successful','reversed',02,0,?,?,'','','')";
                                pstmt=conn.prepareStatement(insertquery);
                                pstmt.setString(1,icicitransid);
                                pstmt.setString(2,refundamount);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,dbpaymentnumber);
                                int j= pstmt.executeUpdate();
                                if(j==1)
                                {   statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"reversed",conn);
                                    sSuccessMessage.append("Success  Treatement(Status:- reversed) for trackingid : "+icicitransid+"<BR>");
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
                }
                catch(Exception e)
                {
                    log.error("EXCEPTION while process transaction",e);
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
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append("<table align=\"center\" border=\"1\" bgcolor=\"CCE0FF\" cellpadding=\"2\" cellspacing=\"2\"><tr><td valign=\"middle\" bgcolor=\"#2379A5\"><font class=\"text\" color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"> Success Result</font></td><td valign=\"middle\" bgcolor=\"#2379A5\"><font class=\"text\" color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"> Fail Result</font></td></tr>");
        chargeBackMessage.append("<tr><td>");
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("</td><td>");
        chargeBackMessage.append(sErrorMessage.toString());
        chargeBackMessage.append("</td></tr></table>");
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        log.debug("forwarding to member preference");

        RequestDispatcher rd = req.getRequestDispatcher("/servlet/QwipiReconcilationList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
