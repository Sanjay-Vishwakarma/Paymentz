package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.RecurringManager;
import com.manager.dao.RecurringDAO;
import com.manager.vo.RecurringBillingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Diksha on 20-Jan-21.
 */
@WebServlet(name = "RbidDetails")
public class RbidDetails extends HttpServlet
{
    private static Logger log = new Logger(RbidDetails.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Enter in RecurringDetails---");

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RecurringBillingVO recurringBillingVO = null;
        RecurringDAO recurringDAO = new RecurringDAO();
        List<RecurringBillingVO> listOfRecurringTransactionVo =null;
        RecurringManager recurringManager = new RecurringManager();

        String partnerid = (String)session.getAttribute("merchantid");
        String rbid = req.getParameter("rbid");
        String terminalid = req.getParameter("terminalid");
        String trackingid = req.getParameter("trackingid");
        try
        {
            recurringBillingVO = recurringDAO.getRecurringSubscriptionDetailsfromRBID(trackingid);
            listOfRecurringTransactionVo = recurringManager.getRecurringTransactionList(trackingid);
        }
        catch (PZDBViolationException dbe)
        {
            log.debug("DB Connection in Recurring Module in merchant---"+dbe);
            PZExceptionHandler.handleDBCVEException(dbe, partnerid, "Recurring Module Merchant Interface");
        }


        req.setAttribute("transactionList",listOfRecurringTransactionVo);
        req.setAttribute("recurringsub",recurringBillingVO);
        req.setAttribute("terminalid",terminalid);
        RequestDispatcher rd = req.getRequestDispatcher("/rbidDetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
