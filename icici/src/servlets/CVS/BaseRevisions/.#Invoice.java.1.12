import com.directi.pg.*;

import com.invoice.dao.InvoiceEntry;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
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

public class Invoice extends HttpServlet
{

    static Logger log = new Logger(Invoice.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Enter in Invoice ");
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");
        ServletContext application = getServletContext();
        String orderid=null;
        String orderdesc=null;
        String trackingid=null;
        String invoiceno=null;

        String status=null;
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String accountid=null;
        String memberid=null;
        int start = 0; // start index
        int end = 0; // end index
        int pageno=1;
        int records=30;
        String str = null;
        String error = "";
        status = Functions.checkStringNull(req.getParameter("status"));

        error =  error + validateOptionalParameters(req);

        memberid =  req.getParameter("memberid");
        orderid = req.getParameter("orderid");
        orderdesc = req.getParameter("orderdesc");
        trackingid = req.getParameter("trackingid");
        invoiceno = req.getParameter("invoiceno");
        fdate = req.getParameter("fdate");
        tdate = req.getParameter("tdate");
        fmonth = req.getParameter("fmonth");
        tmonth =  req.getParameter("tmonth");
        fyear = req.getParameter("fyear");
        tyear = req.getParameter("tyear");

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
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        try
        {

            InvoiceEntry invoiceentry = new InvoiceEntry();
            Set<String> gatewayTypeSet = new HashSet();
            gatewayTypeSet.addAll(invoiceentry.getGatewayHash(null,accountid).keySet());

            Hashtable hash = invoiceentry.listInvoices(memberid, orderid, orderdesc, tdtstamp, fdtstamp, trackingid, invoiceno, status, records, pageno,gatewayTypeSet);
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
        req.setAttribute("errormsg",error);
        RequestDispatcher rd = req.getRequestDispatcher("/invoice.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.ORDERID);
        inputFieldsListOptional.add(InputFields.ORDERDESC);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.INVOICENO);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

}
