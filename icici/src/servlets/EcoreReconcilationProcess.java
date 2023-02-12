import com.directi.pg.*;

import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 3/3/14
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreReconcilationProcess extends HttpServlet
{
    static Logger log = new Logger(EcoreReconcilationProcess.class.getName());
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
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        //variable list
        String[] icicitransidStr =null;

        Collection<Hashtable> listOfRefunds = null;
        String tredtime="";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        tredtime=dateFormat.format(calendar.getTime()) ;
        if (req.getParameterValues("trackingId")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingId");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to EcoreReconcilationList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            log.debug("forwarding to EcoreReconcilationList");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        try
        {
            conn=Database.getConnection();
            for (String icicitransid : icicitransidStr)
            {
                //validate parameters
                try
                {
                    validateOptionalParameter(req);
                    validateMendatoryParameter(req);
                }
                catch (ValidationException e)
                {
                    log.error("validation Exception",e);
                    sErrorMessage.append("Invalid Input Parameter for Trackingid:- "+icicitransid+"<BR>");
                    continue;
                }

                String trackingid = icicitransid;
                String amount = req.getParameter("amount_"+icicitransid);
                String paymentordernumber = req.getParameter("ecorePaymentOrderNumber_" + icicitransid);
                String remark = req.getParameter("remark_" + icicitransid);
                String status = req.getParameter("status_"+icicitransid);
                String desc = req.getParameter("description_"+icicitransid);
                String dbpaymentnumber = req.getParameter("dbpaymentnumber_"+icicitransid);
                String refundamount = req.getParameter("refundamount_"+icicitransid);

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
                    try
                    {

                        String updatequery=null;
                        String insertquery=null;
                        PreparedStatement pstmt=null;
                        if("failed".equalsIgnoreCase(status))
                        {
                            updatequery="UPDATE transaction_ecore SET status='failed' WHERE trackingid=?";
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
                                sErrorMessage.append("Enter ECOREpaymentorder number, Remark for trackingid :"+icicitransid+"<BR>");
                            }
                            else
                            {
                                updatequery="UPDATE transaction_ecore SET ecorePaymentOrderNumber=?, status='authfailed', remark=? WHERE description=? AND trackingid=?";
                                pstmt=conn.prepareStatement(updatequery);
                                pstmt.setString(1,paymentordernumber);
                                pstmt.setString(2,remark);
                                pstmt.setString(3,desc);
                                pstmt.setString(4,icicitransid);
                                int i= pstmt.executeUpdate();
                                if(i==1)
                                {
                                    insertquery="INSERT INTO transaction_ecore_details (parentid,action,status,timestamp,amount,operationCode,responseResultCode,responseDateTime,ecorePaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
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
                                sErrorMessage.append("Enter ECOREpaymentorder number for trackingid :"+icicitransid+"<BR>");
                            }
                            else
                            {
                                updatequery="UPDATE transaction_ecore SET status='capturesuccess',ipcode='2886732298', ipaddress='172.16.10.10', captureamount=?, hrcode='', fixamount=0.75, taxper=0, MID='-1475589951', ecorePaymentOrderNumber=?, ecoreTransactionDateTime=?, remark='Payment Success' WHERE description=? AND trackingid=?";
                                pstmt=conn.prepareStatement(updatequery);
                                pstmt.setString(1,amount);
                                pstmt.setString(2,paymentordernumber);
                                pstmt.setString(3,tredtime);
                                pstmt.setString(4,desc);
                                pstmt.setString(5,icicitransid);
                                int i= pstmt.executeUpdate();
                                if(i==1)
                                {
                                    insertquery="INSERT INTO transaction_ecore_details (parentid,action,status,amount,operationCode,responseResultCode,responseDateTime,ecorePaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) VALUES (?,'Capture Successful','capturesuccess',?,01,1,?,?,'Payment Success',' ',' ')";
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
                                updatequery="UPDATE transaction_ecore SET status='reversed',refundinfo='Admin Treatment by Reconciliation',refundcode='0',refundamount=? WHERE trackingid=? AND STATUS='markedforreversal'";
                                pstmt=conn.prepareStatement(updatequery);
                                pstmt.setString(1,refundamount);
                                pstmt.setString(2,icicitransid);
                                int i= pstmt.executeUpdate();
                                if(i==1)
                                {
                                    insertquery="INSERT INTO transaction_ecore_details(parentid,amount,action,status,operationCode,responseResultcode,responseDateTime,ecorePaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) VALUES (?,?,'Reversal Successful','reversed',02,0,?,?,'','','')";
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

                }
                else
                {
                    sErrorMessage.append("Select Status treatement to process transaction Trackingid:- "+icicitransid+"<BR>");
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error while reversal :",systemError);

            sErrorMessage.append("Error while connecting to Database.");
        }
        finally
        {
            Database.closeConnection(conn);
        }

        req.setAttribute("sSuccessMessage", sSuccessMessage.toString());
        req.setAttribute("sErrorMessage",sErrorMessage.toString());
        log.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/EcoreReconcilationList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ECOREAMOUNT);
        inputFieldsListMandatory.add(InputFields.ECOREPAYMENTORDERNUMBER);

        inputFieldsListMandatory.add(InputFields.ECOREREMARK);
        inputFieldsListMandatory.add(InputFields.ECORESTATUS);
        inputFieldsListMandatory.add(InputFields.ECOREDESC);
        inputFieldsListMandatory.add(InputFields.ECOREDBPAYMENT);
        inputFieldsListMandatory.add(InputFields.ECOREREFUNDAMOUNT);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    private void validateMendatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ECOREDESC);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }
}
