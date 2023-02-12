package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.MerchantConfigManager;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 9/4/2021.
 */
public class AddMerchantTemplateColors extends HttpServlet
{
    private static Logger logger = new Logger(AddMerchantTemplateColors.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session)){
            logger.debug("member is logout");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }

        String errorMsg = "";
        String memberid = req.getParameter("memberid");
        res.setContentType("text/html");
        try{
            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            if (ESAPI.validator().isValidInput("memberid",req.getParameter("memberid"),"OnlyNumber",10,false)){
                Map<String,Object> merchantPresentTemplateDetails = merchantConfigManager.getSavedMemberTemplateDetails(memberid);
                req.setAttribute("merchantTemplateColors",merchantPresentTemplateDetails);
                req.setAttribute("memberid",memberid);
            }else {
                errorMsg = errorMsg + "Invalid Member Id";
            }
        }catch (Exception e){
            logger.error("Error while set Merchant reserves:",e);
        }

        req.setAttribute("errormessage",errorMsg);
        RequestDispatcher rd = req.getRequestDispatcher("/addMerchantTemplateColors.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req,res);
    }
}
