package net.partner;
import com.directi.pg.Logger;
import com.payment.MultiplePartnerUtill;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Admin on 8/27/2018.
 */
public class BinDelete extends HttpServlet
{

    private static Logger logger = new Logger(BinDelete.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
        PartnerFunctions partner = new PartnerFunctions();
        String redirectPage = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String success = "";
        String errormsg = "";
        HashMap hash =null;
        String memberid=req.getParameter("memberid");
        String action = req.getParameter("delete");
        boolean flag=false;

        if (action != null && !action.equals(""))
        {
            String[] ids=req.getParameterValues("id");
            if (action.equalsIgnoreCase("delete"))
            {
                for(String id : ids)
                {
                    flag = multiplePartnerUtill.deleteBin(id);
                }
                if (flag)
                {
                    success = "Records Deleted Successfully for Member ID:"+memberid;
                }
                else
                {
                    errormsg = "Delete failed";
                }
            }
            req.setAttribute("error",errormsg);
            req.setAttribute("success", success.toString());
            redirectPage = "/binRouting.jsp?ctoken=";
        }
        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
}
