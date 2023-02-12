import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.utils.CommonFunctionUtil;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Created by Admin on 2020/05/12.
 */
public class LanguageRedirect extends HttpServlet
{
    private static Logger logger = new Logger(AccountSummary.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String page_name =request.getParameter("page_name");
        String language =request.getParameter("language1");
        RequestDispatcher rd;
        response.setHeader("X-Frame-Options", "ALLOWALL");
        session.setAttribute("X-Frame-Options", "ALLOWALL");

        if(page_name.equals("dashBoard.jsp")){
            rd = request.getRequestDispatcher("/servlet/DashBoard?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("merchprofile.jsp")){
            rd = request.getRequestDispatcher("/servlet/MerchantProfile?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("updatedprofile.jsp")){
            rd = request.getRequestDispatcher("/servlet/MerchantProfile?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("manageMerchantFraudRule.jsp")){
            rd = request.getRequestDispatcher("/servlet/ListMerchantFraudRule?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("transactionDetails.jsp")){
            rd = request.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("generateInvoice.jsp")){
            rd = request.getRequestDispatcher("/servlet/GenerateInvoice?ctoken="+user.getCSRFToken());
        }else if(page_name.equals("invoiceConfiguration.jsp")){
            rd = request.getRequestDispatcher("/servlet/InvoiceConfiguration?ctoken="+user.getCSRFToken());
        }else
        {
            rd = request.getRequestDispatcher("/"+ page_name + "?ctoken=" + user.getCSRFToken());
        }
        if(language.equals("ja"))
        {
            session.setAttribute("language_property", "com.directi.pg.MerchantLanguage_jp");
        }else{
            session.setAttribute("language_property", "com.directi.pg.MerchantLanguage_en");
        }
        /*String attribute="";
        Enumeration<String> attributes = request.getSession().getAttributeNames();
        while (attributes.hasMoreElements()) {
            attribute = (String) attributes.nextElement();
            session.setAttribute(attribute,  request.getSession().getAttribute(attribute));
        }*/

        if (merchants.isLoggedIn(session))
        {
            rd.forward(request,response);
            return;
        }

    }
}
