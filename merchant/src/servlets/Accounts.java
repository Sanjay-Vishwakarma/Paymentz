import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Hashtable;

public class Accounts extends HttpServlet
{

    private static Logger log = new Logger(Accounts.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
    {
        log.debug("Enter in accounts ");
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");

        String desc=null;//Functions.checkStringNull(req.getParameter("desc"));;
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String displayName=(String) session.getAttribute("displayname");
        ServletContext application = getServletContext();
        int pageno=1;
        int records=30;
        try
        {
            desc = ESAPI.validator().getValidInput("desc",req.getParameter("desc"),"SafeString",100,true);
            fdate = ESAPI.validator().getValidInput("fdate",req.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",req.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",req.getParameter("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",req.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",req.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",req.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {
            log.error("Invelid description",e);
        }
        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        PrintWriter out = res.getWriter();
        if(desc!=null)
        {
            if(desc.contains("Withdrawal ("))
            {
                desc = desc.substring(12,desc.length()-1);
            }
            else if(desc.contains("TRF to "))
            {
                String[] temp= desc.split(" for ");
                desc = temp[1].trim();
            }
        }
        //Functions fn=new Functions();
        try
        {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
        records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }

        try
        {
            TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
            log.debug("getting list for transection entry");
            Hashtable hash = transactionentry.listAccounts(desc, tdtstamp, fdtstamp, records, pageno);

            req.setAttribute("Accountsdetails", hash);
            req.setAttribute("balance", transactionentry.getBalance());
            int totalrecords = Integer.parseInt((String) hash.get("records"));

            if (totalrecords == 0)
            {

                req.setAttribute("cfbalance", transactionentry.getSpecialBalance(fdtstamp, 0));
            }
            else
            {
                Hashtable temphash = (Hashtable) hash.get("" + 1);
                int transid = Integer.parseInt((String) temphash.get("transid"));
                String newdtstamp = (String) temphash.get("dtstamp");
                req.setAttribute("cfbalance", transactionentry.getSpecialBalance(newdtstamp, transid));
            }



            log.info("Show all transection to dew date");
            RequestDispatcher rd = req.getRequestDispatcher("/accounts.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError se)
        {
            log.error("Internal Database error",se);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Internal Database error",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
    }
}
