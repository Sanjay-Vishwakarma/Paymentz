import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Vishal on 6/8/2017.
 */
public class PartnerBankMapping extends HttpServlet
{
    public static Logger logger = new Logger(PartnerBankMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        logger.debug("Enter in New partner ");
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        StringBuffer sb = new StringBuffer();
        ApplicationManager applicationManager = new ApplicationManager();

        try
        {
            String bankIDetails= request.getParameter("bankId");
            String bankArr[] = bankIDetails.split("-");
            String bankId = bankArr[0];

            String partnerDetails =  request.getParameter("partnerid");
            String partnerArr[] = partnerDetails.split("-");
            String partnerId =partnerArr[0];

            if (!ESAPI.validator().isValidInput("bankId", bankIDetails, "Description", 255, false))
            {
                sb.append("Invalid Bank Id, kindly provide valid bank id");
            }
            if (!ESAPI.validator().isValidInput("partnerid", partnerDetails, "Description", 255, false))
            {
                sb.append("Invalid Partner Id, kindly provide valid partner id");
            }

            if (sb.length() > 0)
            {
                request.setAttribute("error", sb.toString());
                RequestDispatcher rd = request.getRequestDispatcher("/partnerBankMapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            if(applicationManager.isPartnerBankMappingExist(bankId, partnerId))
            {
                sb.append("Mapping already exist");
            }
            else if (applicationManager.addPartnerBankMapping(bankId, partnerId))
            {
                sb.append("Mapping added successfully");
            }
            else {
                sb.append("Failed to map");
            }

            request.setAttribute("error", sb.toString());
            RequestDispatcher rd = request.getRequestDispatcher("/partnerBankMapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
           logger.error("Catch Exception...",e);
        }
    }
}
