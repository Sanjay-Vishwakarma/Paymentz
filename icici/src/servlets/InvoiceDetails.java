import com.directi.pg.*;

import com.invoice.dao.InvoiceEntry;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Oct 11, 2006
 * Time: 4:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceDetails extends HttpServlet
{   static Logger log = new Logger(InvoiceDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(request, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in TransactionDetails ");
        String errormsg="";
        HttpSession session = req.getSession();


        if (!Admin.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful");

        String merchantid = (String) session.getAttribute("merchantid");
        ServletContext application = getServletContext();

        String status=null;
        String accountid=null;
        int start = 0; // start index
        int end = 0; // end index
        int pageno=1;
        int records=30;
        String str = null;

        status = Functions.checkStringNull(req.getParameter("status"));
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid description",e);
        }
        String orderid = req.getParameter("orderid");
        String orderdesc = req.getParameter("orderdesc");
        String trackingid = req.getParameter("trackingid");
        String invoiceno = req.getParameter("invoiceno");
        //status = ESAPI.validator().getValidInput("status",req.getParameter("status"),"Status",50,true);
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth =  req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

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

        PrintWriter out = res.getWriter();

        //Functions fn=new Functions();
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        try
        {

            InvoiceEntry invoiceentry = new InvoiceEntry();
            Set<String> gatewayTypeSet = new HashSet();
            gatewayTypeSet.addAll(invoiceentry.getGatewayHash(merchantid,accountid).keySet());

            Hashtable hash = invoiceentry.listInvoices(merchantid, orderid, orderdesc, tdtstamp, fdtstamp, trackingid, invoiceno, status, records, pageno,gatewayTypeSet);
            log.debug("Invoices are set successfully ");

            req.setAttribute("transactionsdetails", hash);
            //log.debug(hash);
            log.debug(hash.toString());
            log.info("Redirect to ::::: invoice.jsp");


        }
        catch (SystemError se)
        {
            log.error("System error occure",se);

            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {   log.error("Exception ::::",e);

            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }

        InvoiceEntry invoiceEntry=new InvoiceEntry();
        Hashtable hiddenvariables=invoiceEntry.getInvoiceDetails(req.getParameter("invoiceno"));
        String date=((String)hiddenvariables.get("timestamp")).substring(0,((String)hiddenvariables.get("timestamp")).lastIndexOf(" "));
        String time=((String)hiddenvariables.get("timestamp")).substring(((String)hiddenvariables.get("timestamp")).lastIndexOf(" "),(((String) hiddenvariables.get("timestamp")).length())-1);
        
        hiddenvariables.put("date",date);
        hiddenvariables.put("time",time);

        log.debug(hiddenvariables.toString());

        req.setAttribute("hiddenvariables",hiddenvariables);

        RequestDispatcher view = req.getRequestDispatcher("/invoiceDetails.jsp?ctoken="+user.getCSRFToken());
        log.debug("redirecting to /invoiceDetails.jsp");
        view.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.ORDERID);
        inputFieldsListMandatory.add(InputFields.ORDERDESC);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }


}
