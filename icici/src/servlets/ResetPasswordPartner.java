package servlets;

import com.directi.pg.*;
import com.manager.vo.ActivityTrackerVOs;
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


public class ResetPasswordPartner extends HttpServlet
{
    public static final String ROLE = "partner";
    public static final String SUBPARTNER = "subpartner";
    public static final String CHILDSUBPARTNER = "childsuperpartner";
    public static final String SUPERPARTNER = "superpartner";
    private static Logger log = new Logger(ResetPasswordPartner.class.getName());


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            System.out.println("----------------------36-------");
            log.debug("member is logout");
            response.sendRedirect("/icici/admin/logout.jsp");
            return;
        }

        Functions functions = new Functions();
        Merchants merchants = new Merchants();

        String ctoken = request.getParameter("ctoken");
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String login = request.getParameter("username");
        System.out.println("loginservlet-----" + login);
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String actionExecuterId = (String) session.getAttribute("merchantid");
        System.out.println("actionExecuterId" + actionExecuterId);

        String username = null;
        RequestDispatcher rd = null;
        try
        {
            username = ESAPI.validator().getValidInput("username", request.getParameter("username"), "UserName", 30, false);
            System.out.println("username123" + username);
        }
        catch (ValidationException e)
        {
            String message = "*Invalid Credentials.";
            request.setAttribute("error", message);
            rd = request.getRequestDispatcher("/resetpasswordpartner.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String flagMain = "";
        String isMerchantInterfaceAccess="";

        try
        {


            String userLogin=merchants.getPartnerLoginfromUser(username);
            System.out.println("------------------------userLogin123-------------------" + userLogin);
            if (userLogin != null)
            {
                userLogin = userLogin;
                ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "subpartner");
            }


                           session.setAttribute("Anonymous", user);

                           String role = merchants.getRole(username);
                           System.out.println("roleresetpassword111---------------------12-------------" + role);
                           if (functions.isValueNull(role))
                           {
                               if ("superpartner".equals(role))
                               {
                                   System.out.println("superpartner");

                                   ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "superpartner");
                               }
                               else if ("partner".equals(role))
                               {
                                   System.out.println("partner");

                                   ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "partner");
                               }
                               else if ("subpartner".equals(role))
                               {
                                   System.out.println("subpartner");

                                   ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "subpartner");
                               }
                               else if ("childsuperpartner".equals(role))
                               {
                                   System.out.println("childsuperpartner");

                                   ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "childsuperpartner");
                               }

                           user = ESAPI.authenticator().getUser(username);
                           flagMain = merchants.partnerresetpassword(login, user, remoteAddr, header, actionExecuterId);

                           String forgotPassword = "";
                           String flag = "false";
                           String error = "";
                           if (functions.isValueNull(flagMain))
                           {
                               flag = flagMain.split("_")[0];
                               forgotPassword = flagMain.split("_")[1];
                           }


                           System.out.println("AFTER METHOD----------------" + flag);
                           if (flag.equals("true"))
                           {
                               request.setAttribute("TemparoryPassword", forgotPassword);
                               rd = request.getRequestDispatcher("/resetpasswordpartner.jsp?ctoken=" + user.getCSRFToken());
                               rd.forward(request, response);
                               return;
                           }
                       }

                   else
                  {
                      log.debug("Invalid emailID ");
                request.setAttribute("fmsg","Invalid Username");
                rd = request.getRequestDispatcher("/resetpasswordpartner.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

        }
        catch (Exception e)
        {   log.error("Exception:::::::",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }

    }
    }


