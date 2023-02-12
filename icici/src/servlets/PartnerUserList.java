package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.payment.MultiplePartnerUtill;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

/*
 *Created by IntelliJ IDEA.
 User: Sanjeet
 Date: 15/4/2019
 Time: 12:41 PM
 To change this template use File | Settings | File Templates.
 */

public class PartnerUserList extends HttpServlet
{

    static Logger log = new Logger(PartnerUserList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("in side PartnerUserList---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");

        String errorMsg = "";
        String partnerId = "";
        if (!ESAPI.validator().isValidInput("partnerid", req.getParameter("partnerid"), "Numbers", 10, false))
        {
            errorMsg = errorMsg + "Invalid partnerId";
        }
        else
        {
            partnerId = req.getParameter("partnerid");
        }

        Hashtable detailHash = null;
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
        detailHash = multiplePartnerUtill.getDetailsForSubpartner(partnerId);


        req.setAttribute("detailHash", detailHash);
        req.setAttribute("partnerid", partnerId);
        req.setAttribute("error", errorMsg);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerChildList.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

}
