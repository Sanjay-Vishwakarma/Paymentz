package net.partner;

import com.manager.WhiteListManager;
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
 * Created by vivek on 3/6/2020.
 */
public class DeleteBinCountry extends HttpServlet
{
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        WhiteListManager whiteListManager = new WhiteListManager();
        PartnerFunctions partner = new PartnerFunctions();
        String redirectPage = "";
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String success = "";
        String errormsg = "";
        HashMap hash =null;
        String memberid=request.getParameter("memberid");
        String action = request.getParameter("delete");
        boolean flag=false;

        if (action != null && !action.equals(""))
        {
            String[] ids=request.getParameterValues("id");
            if (action.equalsIgnoreCase("delete"))
            {
                for(String id : ids)
                {
                    flag = whiteListManager.deleteBinCountry(id);
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
            request.setAttribute("error",errormsg);
            request.setAttribute("success", success.toString());
            redirectPage = "/binCountryRouting.jsp?ctoken=";
        }
        RequestDispatcher rd = request.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(request, response);
        return;
    }
}
