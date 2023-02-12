import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import com.invoice.dao.InvoiceEntry;
import com.manager.vo.TerminalVO;
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
import java.text.SimpleDateFormat;
import java.util.*;


public class Invoice extends HttpServlet
{

    private static Logger log = new Logger(Invoice.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        log.debug("Enter in Invoice ");
        Merchants merchants = new Merchants();
        Functions functions = new Functions();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        log.debug("CSRF check successful ");
        String merchantid           = (String) session.getAttribute("merchantid");
        ServletContext application  = getServletContext();
        String orderid              = null;
        String orderdesc            = null;
        String trackingid           = null;
        String terminalid           = null;
        String invoiceno            = null;
        String status               = null;
        String accountid            = null;
        int pageno                  = 1;
        int records                 = 30;
        String errorMessage         = "";

        errorMessage = errorMessage + validateParameters(req);
        if(functions.isValueNull(errorMessage))
        {
            req.setAttribute("error",errorMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/invoice.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        status = Functions.checkStringNull(req.getParameter("status"));

        orderid     = req.getParameter("orderid");
        orderdesc   = req.getParameter("orderdesc");
        trackingid  = req.getParameter("trackingid");
        invoiceno   = req.getParameter("INVOICE_NO");

        Calendar rightNow       = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        Date date               = null;

        res.setContentType("text/html");

        TerminalVO terminalVO = null;
        try
        {
            pageno      = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records     = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno  = 1;
            records = 30;
        }
        try
        {
            String fromdate = req.getParameter("fdate");
            String todate   = req.getParameter("tdate");
            String fdtstamp = "";
            String tdtstamp = "";

            if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
            {
                req.setAttribute("catchError","Invalid From & To date");
                RequestDispatcher rd1 = req.getRequestDispatcher("/invoice.jsp?ctoken="+user.getCSRFToken());
                rd1.forward(req,res);
                return;
            }

            //from Date
            if (functions.isValueNull(fromdate) && functions.isValueNull(todate))
            {
                date            = sdf.parse(fromdate);
                rightNow.setTime(date);
                String fdate    = String.valueOf(rightNow.get(Calendar.DATE));
                String fmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
                String fyear    = String.valueOf(rightNow.get(Calendar.YEAR));

                //to Date
                date            = sdf.parse(todate);
                rightNow.setTime(date);
                String tdate    = String.valueOf(rightNow.get(Calendar.DATE));
                String tmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
                String tyear    = String.valueOf(rightNow.get(Calendar.YEAR));

                log.debug("fmonth " + fmonth);
                log.debug("tmonth " + tmonth);
                 fdtstamp   = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
                tdtstamp    = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            }

            InvoiceEntry invoiceentry   = new InvoiceEntry();
            Set<String> gatewayTypeSet  = new HashSet();
            gatewayTypeSet.addAll(invoiceentry.getGatewayHash(merchantid,accountid).keySet());

            Hashtable hashtable = invoiceentry.listInvoicesForMembers(merchantid, orderid, orderdesc, tdtstamp, fdtstamp, trackingid, invoiceno, status, terminalVO, terminalid, records, pageno, gatewayTypeSet, (String) session.getAttribute("role"), user.getAccountName());
            log.debug("Invoices are set successfully ");

            req.setAttribute("transactionsdetails", hashtable);
            req.setAttribute("status",status);
            log.debug(hashtable.toString());
            log.info("Redirect to ::::: invoice.jsp");

        }
        catch (SystemError se)
        {
            log.error("System error occure",se);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {   log.error("Exception ::::",e);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/invoice.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error    = "";
        String EOL      = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.ORDERID);
        inputFieldsListOptional.add(InputFields.ORDERDESC);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.INVOICE_NO);
        inputFieldsListOptional.add(InputFields.STATUS);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }
}