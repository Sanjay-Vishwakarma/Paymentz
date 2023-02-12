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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class ResetPassword extends HttpServlet
{
    public static String MERCHANT           = "merchant";
    public static String SUBMERCHANT           = "submerchant";

    private static Logger log = new Logger(ResetPassword.class.getName());


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
        String role           = MERCHANT;
        String roles           =SUBMERCHANT;

/*
        log.error("user name---" + user.getAccountName());
        log.error("user name---" + user);*/

        if (!Admin.isLoggedIn(session))
        {
            System.out.println("----------------------36-------");
            log.debug("member is logout");
            response.sendRedirect("/icici/admin/logout.jsp");
            return;
        }

        Merchants merchants = new Merchants();
        Functions functions = new Functions();
        String ctoken = request.getParameter("ctoken");
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String merchantid = request.getParameter("merchantid");
        String login=request.getParameter("username");
        System.out.println("loginservlet-----"+login);
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String actionExecuterId = (String) session.getAttribute("merchantid");
        String memberid = request.getParameter("memberid");


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
            rd = request.getRequestDispatcher("/resetpassword.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }


        String activation = null;


        String flagMain = "";
        String MERCHANT = "merchant";
        String SUBMERCHANT = "submerchant";
        // Connection connection = null;
        //ResultSet rs = null;
        try
        {
            System.out.println("------------------------try1-------------------");
            String parentLogin = username;
            System.out.println("------------------------parentLogin-------------------" + parentLogin);
            String userLogin = merchants.getMemberLoginfromUser(username);
            System.out.println("------------------------userLogin-------------------" + userLogin);

            if (userLogin != null)
            {
                parentLogin = userLogin;
                request.setAttribute("role", "submerchant");
            }
            //connection = Database.getConnection();
            String role1 = (String) request.getAttribute("role");
            System.out.println("role1-------------------"+role1);
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
            System.out.println("isMerchantInterfaceAccesspartner----------------------" + isMerchantInterfaceAccess);
            long diffHours = 1;
            if (functions.isValueNull(forgotDate))
            {
                System.out.println("forgotdate---------------" + forgotDate);
                forgotDate = Functions.convertDtstampToDBFormat(forgotDate);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date d1 = sdf.parse(forgotDate);
                Date d2 = sdf.parse(currentDate);
                long diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
            }

            session.setAttribute("Anonymous", user);
            String fpassword="";

            System.out.println("diff-------------" + diffHours);
            if (user != null)
            {
                System.out.println("user name---" + user.getAccountName());
                System.out.println("user name---" + user.getRoles());
//            todo uncheck if condition before commiting file
               /* if (diffHours >= 1)
                {*/
                System.out.println("difff>=1");
                ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();

                if (isMerchantInterfaceAccess.equals("Y"))
                {
                    System.out.println("merchantinterface-------11--------------");
                    System.out.println("username---------------------" + username);
                   // if("true".equalsIgnoreCase(resetpasswordforrole))
                     boolean users=merchants.resetpasswordforrole(login,memberid);
                    if("true".equals(users))
                    {
                        ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "submerchant");
                    }
                    else
                    {
                        ESAPI.httpUtilities().getCurrentRequest().setAttribute("role", "merchant");
                    }
                    user = ESAPI.authenticator().getUser(username);
                    System.out.println("user--------------------------before method------" + user.getAccountName());
                    flagMain = merchants.resetpassword(username,user, remoteAddr, header, actionExecuterId,role,memberid);
                    String forgotPassword = "";
                    String flag = "false";
                    String error="";
                    if(functions.isValueNull(flagMain))
                    {
                        flag = flagMain.split("_")[0];
                        forgotPassword = flagMain.split("_")[1];
                    }


                    System.out.println("AFTER METHOD----------------" + flag);
                    if (flag.equals("true"))
                    {
                        System.out.println("trueflag");
                        request.setAttribute("TemparoryPassword", forgotPassword);
                        rd = request.getRequestDispatcher("/resetpassword.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }

                }
                else
                {
                    System.out.println("emailid11111111111");
                    log.debug("Invalid emailID ");
                    request.setAttribute("fmsg","Invalid Username");
                    rd = request.getRequestDispatcher("/resetpassword.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                log.debug("forgot password greater than 1 hour ");

//           todo uncheck else condition before commiting file

               /* }
                else
                {
                    log.debug("forgot password less than 1 hour ");
                    redirectpage = "/resetpassword.jsp?ctoken=" + ctoken;
                }*/


           /* }
            else
            {
                log.debug("Invalid emailID ");
                request.setAttribute("action", "T");
                request.setAttribute("error", "Unauthorized User");
                ///redirectpage = "/forgotpwd.jsp?ctoken=" + ctoken;
                redirectpage = "/fpwdsentmail.jsp?ctoken=" + ctoken;
            }*/
               // rd = request.getRequestDispatcher(redirectpage);
               // rd.forward(request, response);
            }
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
