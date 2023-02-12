import com.directi.pg.*;
import com.fraud.FraudTransaction;

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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import java.util.*;


public class FraudTransactionList extends HttpServlet
{
    static Logger logger = new Logger(FraudTransactionList.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        /*logger.debug("Entering in FraudTransactionLIst doGet()");
        String fstransid =req.getParameter("fstransid");
        String fsid=req.getParameter("fsid");
        FraudTransaction fraudTransaction=new FraudTransaction();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        if (fstransid == null || fstransid.equals("") ||fstransid.equals("null") ||fsid == null || fsid.equals("")|| fsid.equals("null"))
            doPost(req, res);
        try
        {
            Hashtable hash =fraudTransaction.getFraudTransactionDetails(fstransid,fsid);
            req.setAttribute("transactionsdetails", hash);
            logger.debug("Forwarding to fraudTransactionDetails.jsp");
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("SysyemError in doGet method",se);
        }
        catch (Exception e)
        {
            logger.error("Exception in doGet method", e);
        }*/

        doPost(req, res);

    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("listing Fraud Transaction in doPost method");
        int start = 0; // start index
        int end = 0; // end index
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        FraudTransaction fraudTransaction=new FraudTransaction();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        if(functions.isValueNull(req.getParameter("fstransid")) && functions.isValueNull(req.getParameter("fsid")))
        {
            try
            {
                Hashtable hash =fraudTransaction.getFraudTransactionDetails(req.getParameter("fstransid"),req.getParameter("fsid"),req.getParameter("score"));
                req.setAttribute("transactionsdetails", hash);
                logger.debug("Forwarding to fraudTransactionDetails.jsp");
                RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (SystemError se)
            {
                logger.error("SysyemError in doGet method",se);
            }
            catch (Exception e)
            {
                logger.error("Exception in doGet method", e);
            }
            return;
        }
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            logger.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        String fsid = req.getParameter("fsid");
        String fstransstatus = req.getParameter("fstransstatus");
        String trackingid = req.getParameter("STrackingid");
        String fstransid = req.getParameter("fraudtransid");
        String toid = req.getParameter("toid");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);

       try
        {
            Hashtable hash =fraudTransaction.listFraudTransactions1(toid,trackingid,fstransid,fsid,tdtstamp, fdtstamp, fstransstatus,records, pageno);
            req.setAttribute("transactionsdetails",hash);
            logger.debug("Transaction is successfully load the data and forwaring to fraudtransactionList.jsp");
        }

        catch (SystemError se)
        {
            logger.error("errror in doPost ", se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            logger.error("Exception:::: errror in doPost", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TRACKINGID_TRA);
        inputFieldsListMandatory.add(InputFields.STATUS);
        inputFieldsListMandatory.add(InputFields.TOID);
        //inputFieldsListMandatory.add(InputFields.FSID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
