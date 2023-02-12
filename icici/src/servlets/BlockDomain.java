import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class BlockDomain extends HttpServlet
{
    private static Logger logger = new Logger(BlockDomain.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        logger.debug("Entering in block Domain");
        res.setContentType("text/html");
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();
        int count = 0;
        int result = 0;
        String incorrectdomain = "";
        String repeatdomain = "";
        Connection con = null;
        PreparedStatement pstmt=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            for (int i = 1; i <= 10; i++)
            {
                String domain = Functions.checkStringNull(req.getParameter("domain" + i));
                if (domain != null)
                {
                    if (!ESAPI.validator().isValidInput("domain",req.getParameter("domain" + i),"URL",50,false))
                    {
                        incorrectdomain = incorrectdomain + domain + "<br>";
                        continue;
                    }
                    con = Database.getConnection();
                    String query = "select * from blockedemail where emailaddr=?";
                    pstmt=con.prepareStatement(query);
                    pstmt.setString(1,domain);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        repeatdomain = repeatdomain + domain + "<br>";
                        continue;
                    }
                    StringBuffer domainquery = new StringBuffer("insert into blockedemail(emailaddr,type) values(?,'domain')");

                    ps=con.prepareStatement(domainquery.toString());
                    ps.setString(1,domain);
                    result = ps.executeUpdate();
                    if (result > 0)
                    {
                        count++;
                    }
                }
            }
            String message = "";
            if (Functions.checkStringNull(incorrectdomain) != null)
                message = "Below domains are not blocked as domain names are incorrect.<br>" + incorrectdomain;

            if (Functions.checkStringNull(repeatdomain) != null)
                message = message + "Below domains are already blocked.<br>" + repeatdomain;

            if (count > 0)
            {
                sSuccessMessage.append("domain names are blocked. <br>  \r\n"+ message);
            }
            else
                sErrorMessage.append("No domain name is blocked.<br> \r\n"+ message);
        }
        catch (SystemError se)
        {   logger.error("System Error::: in BlockDomain",se);
            sErrorMessage.append("Internal Error while Block Domain<br> \r\n");
        }
        catch (Exception e)
        {   logger.error("Exeption in BlockDomain::::",e);
            sErrorMessage.append("Internal Error while Block Domain<br> \r\n");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedDomainList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.DOMAIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
