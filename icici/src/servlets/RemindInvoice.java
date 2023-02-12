import com.directi.pg.*;

import com.invoice.dao.InvoiceEntry;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;


public class RemindInvoice extends HttpServlet
{

    private static Logger log = new Logger(RemindInvoice.class.getName());
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
        String ctoken=  user.getCSRFToken();

        int invoiceno=0;
        invoiceno=Integer.parseInt((String)req.getParameter("invoiceno"));

        InvoiceEntry invoiceEntry = new InvoiceEntry();
        try{

            invoiceEntry.sendInvoiceMail(invoiceno+"" );

        }
        catch(Exception e)
        {
            String error="We are Unable to Send an E-Mail at this moment <BR> Kindly Try Again After Some Time";
            log.error("Exception Occured while Sending Mail",e);
            req.setAttribute("error",error);
        }
        req.setAttribute("invoiceno",invoiceno);
        req.setAttribute("remindercounter",Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno+"")).get("remindercounter")));
        log.debug("creating rd");
        RequestDispatcher rd = req.getRequestDispatcher("/remindInvoice.jsp?ctoken="+ctoken);

        log.debug("forwarding request to invoice generated");
        rd.forward(req, res);
    
    
    
    
    
    
    }
}