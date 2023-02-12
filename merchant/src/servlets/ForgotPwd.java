import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ForgotPwd extends HttpServlet
{
    private static Logger log = new Logger(ForgotPwd.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user = (User) session.getAttribute("Anonymous");

        Merchants merchants = new Merchants();
        Functions functions = new Functions();
        String ctoken = request.getParameter("ctoken");
        String partnerid=(String)session.getAttribute("partnerid");
        log.error("PARTNER ID FROM forgot password  "+partnerid);
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String actionExecuterId=(String) session.getAttribute("merchantid");

        if (Functions.validateCSRFAnonymos(request.getParameter("ctoken"), user))
        {

            session.setAttribute("Anonymous", user);

        }
        else
        {
            log.debug("session out");
            response.sendRedirect("/merchant/sessionout.jsp");

            return;
        }

        String redirectpage = null;
        String username = null;

        RequestDispatcher rd = null;
        try
        {
            username = ESAPI.validator().getValidInput("username", request.getParameter("username"), "SafeString", 100, false);
        }

        catch (ValidationException e)
        {
            String message = "*Invalid Credentials.";
            request.setAttribute("error", message);
            rd = request.getRequestDispatcher("/forgotpwd.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String activation = null;
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        boolean flag = false;
        String MERCHANT ="merchant";
        String SUBMERCHANT ="submerchant";
        String role = "merchant";

       // Connection connection = null;
        //ResultSet rs = null;
        try
        {
            String parentLogin = username;
            String userLogin = merchants.getMemberLoginfromUser(username);
            if(userLogin!=null)
            {
                parentLogin = userLogin;
                request.setAttribute("role","submerchant");
            }
            //connection = Database.getConnection();
            String role1 = (String)request.getAttribute("role");
            String isMerchantInterfaceAccess = "";
            String forgotDate = "";
            if (role1.equals("submerchant"))
            {
                forgotDate = merchants.getFdtstamp(username, role1);
            }
            else
            {
                forgotDate = merchants.getFdtstamp(parentLogin, role1);
            }
            isMerchantInterfaceAccess = merchants.getMemberIcici(parentLogin);
                long diffHours = 1;
                if (functions.isValueNull(forgotDate))
                {
                    forgotDate = Functions.convertDtstampToDBFormat(forgotDate);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Date d1 = sdf.parse(forgotDate);
                    Date d2 = sdf.parse(currentDate);
                    long diff = d2.getTime() - d1.getTime();
                    diffHours = diff / (60 * 60 * 1000);
                }
                if (diffHours >= 1)
                {
                    if (isMerchantInterfaceAccess.equals("Y"))
                    {
                        user = ESAPI.authenticator().getUser(username);
                        flag = merchants.forgotPasswordNew(username, user,remoteAddr,header,actionExecuterId,partnerid);
                        if (flag == true)
                        {

                            request.setAttribute("ctoken", ctoken);
                            redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
                            log.info("send mail on merchant id on this page" + redirectpage);
                        }
                        else
                        {
                            log.debug("Invalid emailID ");
                            request.setAttribute("action", "F");
                            //redirectpage = "/forgotpwd.jsp?ctoken=" + ctoken;
                            redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
                        }
                    }
                    else
                    {
                        log.debug("Unauthorized user");
                        redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
                    }
                    log.debug("forgot password greater than 1 hour ");
                }

                else
                {
                    log.debug("forgot password less than 1 hour ");
                    redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
                }
           /* }
            else
            {
                log.debug("Invalid emailID ");
                request.setAttribute("action", "T");
                request.setAttribute("error", "Unauthorized User");
                ///redirectpage = "/forgotpwd.jsp?ctoken=" + ctoken;
                redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
            }*/
            rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

        }
        catch (SystemError se)
        {
            log.error("System Error:::::::", se);
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {
            log.error("Exception:::::::", e);
            out.println(Functions.NewShowConfirmation1("Error!", e.toString()));
        }
      /*  finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p1);
            Database.closeConnection(connection);
        }*/

    }
}
