import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import javacryption.aes.AesCtr;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

public class changePassword extends HttpServlet
{
    static Logger log=new Logger("logger1");
    static String classname= changePassword.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = Functions.getNewSession(request);
        if(!CustomerSupport.isLoggedIn(session))
        {
            response.sendRedirect("/support/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PrintWriter out = response.getWriter();
        log.info("ENTER Changing Password");
        RequestDispatcher rd;
        String oldPass=null;
        String newPass=null;
        String confPass=null;
        Hashtable error=null;

        if(request.getParameter("oldPass")!=null)
        {
            oldPass= AesCtr.decrypt(request.getParameter("oldPass"), request.getParameter("ctoken"), 256);
            request.setAttribute("oldpwd",oldPass);        }
        if(request.getParameter("newPass")!=null)
        {
            newPass= AesCtr.decrypt(request.getParameter("newPass"), request.getParameter("ctoken"), 256);
            log.debug("new Password after decryption::"+newPass);
            request.setAttribute("newpwd",newPass);
        }
        if(request.getParameter("confPass")!=null)
        {
            confPass= AesCtr.decrypt(request.getParameter("confPass"), request.getParameter("ctoken"), 256);
            log.debug("confirmation Password after decryption::"+confPass);
            request.setAttribute("confirmpwd",confPass);
        }

        Integer csId  =(Integer) session.getAttribute("csId");
        String redirectPage="";
        Hashtable changepass=new Hashtable();
        changepass.put(InputFields.OLD_PASSWORD,oldPass);
        changepass.put(InputFields.NEW_PASSWORD,newPass);
        changepass.put(InputFields.CONFIRM_PASSWORD,confPass);
        error= CustomerSupport.validateMandatoryParameters(changepass);
        try
        {
            if(!CustomerSupport.checkOldPass(user.getAccountId(), oldPass))
            {
                rd=request.getRequestDispatcher("/changePassword.jsp?MES=OLD&ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
        }
        catch (SystemError systemError)
        {
            log.error(classname+" System error in old Password check::",systemError); //To change body of catch statement use File | Settings | File Templates.
        }
         if(!error.isEmpty())
         {
             request.setAttribute("error",error);
             rd=request.getRequestDispatcher("/changePassword.jsp?MES=X&ctoken="+user.getCSRFToken());
             rd.forward(request,response);
             return;
         }




        if(newPass.equals(confPass))
            {
                if(CustomerSupport.changePassword(oldPass, newPass, user.getAccountId(), user))
                {
                  redirectPage="/changePassword.jsp?MES=Y&ctoken="+user.getCSRFToken();
                }else
                {
                  redirectPage="/changePassword.jsp?MES=N&ctoken="+user.getCSRFToken();
                }




            }
            else{ log.debug("new PASSWORD and CONFIRM PASSWORD not matching");
                redirectPage="/changePassword.jsp?MES=C&ctoken="+user.getCSRFToken();

            }
        rd = request.getRequestDispatcher(redirectPage);
        rd.forward(request,response);
}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request,response);
    }
}
