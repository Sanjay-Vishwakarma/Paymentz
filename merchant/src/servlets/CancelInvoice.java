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

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 29/12/12
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CancelInvoice extends HttpServlet
{

    private static Logger log = new Logger(CancelInvoice.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        log.debug("Enter in Invoice ");
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");

        int invoiceno=0;
        invoiceno=Integer.parseInt((String)req.getParameter("invoiceno"));
        String reason="";
        reason=(String)req.getParameter("cancelreason");
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        invoiceEntry.cancelInvoice(invoiceno+"",reason);


        req.setAttribute("invoiceno",invoiceno);
        req.setAttribute("cancelreason",reason);
        RequestDispatcher rd = req.getRequestDispatcher("/cancelInvoice.jsp?ctoken="+user.getCSRFToken());
        log.debug("Forwarding to cancel.jsp");
        rd.forward(req, res);


    }
}
