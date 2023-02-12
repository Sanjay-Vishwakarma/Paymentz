import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Oct 11, 2006
 * Time: 4:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceDetails extends HttpServlet
{   private static Logger log = new Logger(InvoiceDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(request, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   log.debug("Entering in TransactionDetails ");
        String errormsg="";
        HttpSession session = req.getSession();

        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful");

        String merchantid = (String) session.getAttribute("merchantid");
        ServletContext application = getServletContext();
        String orderid      = null;
        String orderdesc    = null;
        String trackingid   = null;
        String terminalid   = null;
        String invoiceno    = null;

        String status       = null;
        String fdate        = null;
        String tdate        = null;
        String fmonth       = null;
        String tmonth       = null;
        String fyear        = null;
        String tyear        = null;
        String accountid    = null;
        int start       = 0; // start index
        int end         = 0; // end index
        int pageno      = 1;
        int records     = 30;
        String str      = null;
        boolean valid  = true;
        String action     = "";


        status = Functions.checkStringNull(req.getParameter("status"));

        try
        {
            if(req.getParameter("action") != null){
                action = req.getParameter("action");
            }

            if(action.equalsIgnoreCase("search")){
                valid = false;
            }

            orderid     = ESAPI.validator().getValidInput("orderid",req.getParameter("orderid"),"Description",100,true);
            orderdesc   = ESAPI.validator().getValidInput("orderdesc",req.getParameter("orderdesc"),"Description",100,true);
            trackingid  = ESAPI.validator().getValidInput("trackingid",req.getParameter("trackingid"),"Numbers",20,true);
            terminalid  = ESAPI.validator().getValidInput("terminalid",req.getParameter("terminalid"),"Numbers",5,true);
            invoiceno   = ESAPI.validator().getValidInput("invoiceno",req.getParameter("invoiceno"),"Numbers",20,true);
            //status = ESAPI.validator().getValidInput("status",req.getParameter("status"),"Status",50,true);
            if(valid){
                fdate       = ESAPI.validator().getValidInput("fdate",req.getParameter("fdate"),"Days",2,true);
                tdate       = ESAPI.validator().getValidInput("tdate",req.getParameter("tdate"),"Days",2,true);
            }
            fmonth      = ESAPI.validator().getValidInput("fmonth",req.getParameter("fmonth"),"Months",2,true);
            tmonth      =  ESAPI.validator().getValidInput("tmonth",req.getParameter("tmonth"),"Months",2,true);
            fyear       = ESAPI.validator().getValidInput("fyear",req.getParameter("fyear"),"Years",4,true);
            tyear       = ESAPI.validator().getValidInput("tyear",req.getParameter("tyear"),"Years",4,true);

        }
        catch(ValidationException e)
        {
            log.error("Invalid description",e);
        }
        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


        log.debug("fmonth " + fmonth);
        log.debug("tmonth " + tmonth);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        PrintWriter out                 = res.getWriter();
        TerminalVO terminalVO           = null;
        TerminalManager terminalManager = new TerminalManager();
        try
        {
            terminalVO = terminalManager.getTerminalByTerminalId(terminalid);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException--->",e);
        }
        //Functions fn=new Functions();
        try
        {
            pageno  = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno  = 1;
            records = 30;
        }
        try
        {
            InvoiceEntry invoiceentry   = new InvoiceEntry();
            Set<String> gatewayTypeSet  = new HashSet();
            gatewayTypeSet.addAll(invoiceentry.getGatewayHash(merchantid,accountid).keySet());

            //Hashtable hash = invoiceentry.listInvoices(merchantid, orderid, orderdesc, tdtstamp, fdtstamp, trackingid, invoiceno, status, records, pageno,gatewayTypeSet);
            //Hashtable hashtable = invoiceentry.listInvoicesForMembers(merchantid, orderid, orderdesc, tdtstamp, fdtstamp, trackingid, invoiceno, status,terminalVO, terminalid, records, pageno,gatewayTypeSet,(String) session.getAttribute("role"), user.getAccountName());
            log.debug("Invoices are set successfully ");

            //req.setAttribute("transactionsdetails", hash);
            //req.setAttribute("transactionsdetails", hashtable);
            //log.debug(hash);
            //log.debug(hashtable.toString());
            log.info("Redirect to ::::: invoice.jsp");
        }
        catch (Exception e)
        {   log.error("Exception ::::",e);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }

        InvoiceEntry invoiceEntry   = new InvoiceEntry();
        Hashtable hiddenvariables   = invoiceEntry.getInvoiceDetails(req.getParameter("invoiceno"));
        String date                 = ((String)hiddenvariables.get("timestamp")).substring(0,((String)hiddenvariables.get("timestamp")).lastIndexOf(" "));
        String time                 = ((String)hiddenvariables.get("timestamp")).substring(((String)hiddenvariables.get("timestamp")).lastIndexOf(" "),(((String) hiddenvariables.get("timestamp")).length())-1);
        
        hiddenvariables.put("date",date);
        hiddenvariables.put("time",time);
        Functions functions         = new Functions();
        Hashtable transactionHash   = new Hashtable();

        if (functions.isValueNull((String) hiddenvariables.get("trackingid")))
        {
            transactionHash = getTransactionDetails(merchantid, (String) hiddenvariables.get("trackingid"));
        }

        req.setAttribute("transactionDetails",transactionHash);


        log.debug(hiddenvariables.toString());



        req.setAttribute("hiddenvariables",hiddenvariables);


        RequestDispatcher view = req.getRequestDispatcher("/invoiceDetails.jsp?ctoken="+user.getCSRFToken());
        log.debug("redirecting to /invoiceDetails.jsp");
        view.forward(req, res);


    }

    private Hashtable getTransactionDetails(String memberId, String iciciTransId)
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash          = null;
        Connection conn         = null;
        PreparedStatement pstmt = null;

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query =null;

                query = "select status,remark as authqsiresponsecode,trackingid as icicitransid ,name ,paymentid, amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') as \"date\",td.emailaddr as email,td.city as city,street,td.state as state,td.country as country from transaction_common td,members where td.toid=members.memberid and trackingid= ? and toid = ?";

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            log.debug("transaction detail query----"+pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

        }
        catch (SQLException e)
        {
            log.error("SystemError----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        ctx.log("Leaving getTransactionDetails");
        return hash;
    }




}