import com.directi.pg.Admin;
import com.directi.pg.AsyncActivityTracker;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AdminManager;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.AdminDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 03/11/15
 * Time: 03:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewAdminSignUp extends HttpServlet
{
    private static Logger log = new Logger(NewAdminSignUp.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String msg = "";
        String EOL="<BR>";
        StringBuffer sb=new StringBuffer();
        Functions functions=new Functions();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        String passwd=req.getParameter("passwd");
        String conpasswd=req.getParameter("conpasswd");
        String userName=req.getParameter("username");
        String contactEmails=req.getParameter("contact_emails");

        if(functions.isValueNull(passwd))
        {
            passwd = AesCtr.decrypt(req.getParameter("passwd"), req.getParameter("ctoken"), 256);

        }
        if(functions.isValueNull(conpasswd))
        {
            conpasswd = AesCtr.decrypt(req.getParameter("conpasswd"), req.getParameter("ctoken"), 256);
        }
        if (!ESAPI.validator().isValidInput("username",req.getParameter("username"),"alphanum",30,false))
        {
            msg="please enter valid username."+EOL;
            log.error(msg);
            sb.append(msg);
        }
        if ((!ESAPI.validator().isValidInput("passwd",passwd,"NewPassword",20,false)))
        {
            msg="please enter valid password"+EOL;
            log.error(msg);
            sb.append(msg);
        }
        if ((!ESAPI.validator().isValidInput("contactEmails",contactEmails,"Email",200,true)))
        {
            msg="please enter valid email address"+EOL;
            log.error(msg);
            sb.append(msg);
        }
        if ((!ESAPI.validator().isValidInput("conpasswd",conpasswd,"NewPassword",20,false)) || (!(passwd).equals(conpasswd)))
        {
            msg="please enter valid password & confirm password."+EOL;
            log.error(msg);
            sb.append(msg);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/adminsignup.jsp?ctoken="+user.getCSRFToken());
        if(sb.length()>0)
        {
            req.setAttribute("message",sb.toString());
            rd.forward(req, res);
            return;
        }
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        AdminDetailsVO adminDetailsVO=new AdminDetailsVO();
        adminDetailsVO.setLogin(userName);
        adminDetailsVO.setContactEmails(contactEmails);
        adminDetailsVO.setPassword(passwd);

        AdminManager adminManager=new AdminManager();
        try
        {
            if(adminManager.isUniqueLoginName(adminDetailsVO))
            {
                try
                {
                    req.setAttribute("role","admin");
                    User adminUser =ESAPI.authenticator().createUser(userName, passwd, "admin");
                    String status=adminManager.addNewAdminUser(adminDetailsVO,adminUser);
                    if("success".equalsIgnoreCase(status))
                    {
                        msg=adminUser.getAccountName()+" Admin Is Created  Successfully";
                        String remoteAddr = Functions.getIpAddress(req);
                        int serverPort = req.getServerPort();
                        String servletPath = req.getServletPath();
                        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                         activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                            activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                            activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                            activityTrackerVOs.setAction(ActivityLogParameters.ADD.toString());
                            activityTrackerVOs.setModule_name(ActivityLogParameters.ADD_NEW_ADMIN.toString());
                            activityTrackerVOs.setLable_values("Username="+userName+",Contact email address="+contactEmails);
                            activityTrackerVOs.setDescription(ActivityLogParameters.USER_CREATED.toString() + "-" + userName );
                            activityTrackerVOs.setIp(remoteAddr);
                            activityTrackerVOs.setHeader(header);
                            try
                            {
                                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                                asyncActivityTracker.asyncActivity(activityTrackerVOs);
                            }
                            catch (Exception e)
                            {
                                log.error("Exception while AsyncActivityLog::::", e);
                            }
                    }
                    else
                    {
                        msg="New Admin User Creation Failed ";
                    }
                }
                catch (Exception e)
                {
                    log.error("Add user throwing Authentication Exception ", e);
                    String message="";
                    if(e instanceof AuthenticationAccountsException)
                    {
                        message=((AuthenticationAccountsException)e).getLogMessage();
                        if(message.contains("Duplicate"))
                        {
                            msg="username is already used please provide unique username";
                            req.setAttribute("message",msg);
                            rd.forward(req,res);
                            return;
                        }

                    }

                    try
                    {
                        adminManager.removeAdminUserEntries(adminDetailsVO);
                            msg = "New Admin User Creation Failed";
                    }
                    catch(Exception e1)
                    {
                        msg="New Admin User Creation Failed";
                        log.error("Exception while deletion of Details::",e1);
                    }
                }
            }
            else
            {
                msg="username is already used please provide unique username";
            }
            req.setAttribute("message",msg);
            rd.forward(req,res);
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in NewAdminSignUp.java------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "");
            req.setAttribute("message","Internal Error occurred : Please contact your Admin");
            rd.forward(req,res);
            return;
        }

    }

}
