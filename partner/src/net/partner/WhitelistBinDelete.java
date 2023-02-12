package net.partner;

import com.directi.pg.Functions;
import com.manager.dao.WhiteListDAO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 4/19/2019.
 */
public class WhitelistBinDelete extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        HttpSession session= Functions.getNewSession(request);
        if (!partnerFunctions.isLoggedInPartner(session)){

            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String memberid="";
        String msg="";
        String errormsg="";
        String ids[]=request.getParameterValues("id");
        for (String id:ids)
        {
             memberid=request.getParameter("memberid_"+id);
            try
            {
              boolean flag=whiteListDAO.deleteWhitelistBin(id);
                if (flag)
                {
                    msg= "Records Deleted Successful for Member ID:" + memberid;
                }
                else
                {
                    errormsg = "Delete failed";
                }
            }
            catch (Exception e)
            {

            }
        }
        request.setAttribute("error",errormsg);
        request.setAttribute("msg",msg.toString());
        RequestDispatcher rd= request.getRequestDispatcher("/whitelistBin.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
        return;
    }

}
