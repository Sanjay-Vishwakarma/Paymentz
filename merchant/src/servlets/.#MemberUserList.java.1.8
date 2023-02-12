import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.payment.MultipleMemberUtill;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

//import com.payment.exceptionHandler.PZDBViolationException;
//import com.payment.validators.InputFields;
//import com.payment.validators.InputValidator;
//import org.owasp.esapi.ValidationErrorList;
//import java.util.ArrayList;
//import java.util.List;

/**
 * Created by Admin on 1/9/2016.
 */
public class MemberUserList extends HttpServlet
{
    private static Logger log = new Logger(MemberUserList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("in side MemberUserList---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        res.setContentType("text/html");
        String memberid = (String) session.getAttribute("merchantid");
        //String errormsg = "";
        Hashtable detailHash = null;
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        String role = (String)session.getAttribute("role");
        String Username = (String)session.getAttribute("username");
        if(role.contains("submerchant")){
            detailHash = multipleMemberUtill.getDetailsForSubmerchantUser(memberid, Username);
            System.out.println("inside if cond==");
            log.error("inside if cond==");
        }else
        {
            detailHash = multipleMemberUtill.getDetailsForSubmerchant(memberid);
//            System.out.println("inside else cond==");
            log.error("inside else cond==");

        }


        //errormsg=errormsg+validateParameters(req);

        /*if(errormsg!=null && !errormsg.equals(""))
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }*/

        req.setAttribute("detailHash",detailHash);
        req.setAttribute("merchantid",memberid);
        RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    /*private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.MERCHANTID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }*/

}
