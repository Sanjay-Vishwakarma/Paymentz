import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.payment.MultipleMemberUtill;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

//import com.payment.exceptionHandler.PZDBViolationException;
//import org.owasp.esapi.ESAPI;
//import org.owasp.esapi.errors.ValidationException;


/**
 * Created by Admin on 1/9/2016.
 */
public class EditMemberUserList extends HttpServlet
{
    private static Logger log = new Logger(EditMemberUserList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("inside EditMemberUserList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Merchants merchants = new Merchants();
        Functions functions=new Functions();
        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        res.setContentType("text/html");

        String login = req.getParameter("login");
        String memberid = req.getParameter("merchantid");
        //System.out.println("merchantid in edit member user list---"+req.getParameter("merchantid"));
        String errormsg = "";
        String email = "";
        String telno="";
        String telcc="";
        Hashtable hash = null;
        String redirectPage = "";
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        if (req.getParameter("action").equalsIgnoreCase("view") || req.getParameter("action").equalsIgnoreCase("modify1"))
        {
            hash = multipleMemberUtill.viewDetailsForMemberUserList(login);

            req.setAttribute("detailHash",hash);
            req.setAttribute("login",login);
            req.setAttribute("action",req.getParameter("action"));
            req.setAttribute("error",errormsg);
            redirectPage = "/viewMemberUserList.jsp?ctoken=";
        }

        if (req.getParameter("action").equalsIgnoreCase("modify"))
        {
            Hashtable hash1 = new Hashtable();
            //memberid = req.getParameter("merchantid");

            /*email = (String)hash.get("emailaddress");*/

            errormsg = errormsg+validateMandatoryParameters(req);
            if((functions.isValueNull(req.getParameter("telnocc")) && (req.getParameter("telnocc").length()<1) || (req.getParameter("telnocc").length() > 4))||
                    !functions.isValueNull(req.getParameter("telnocc")) ||
                    !functions.isValueNull(req.getParameter("telnocc")) || !ESAPI.validator().isValidInput(req.getParameter("telnocc"),req.getParameter("telnocc"), "OnlyNumber",4, false)){

                errormsg=errormsg + "Invalid Phone CC"+"<BR>";
            }

            if((functions.isValueNull(req.getParameter("telno")) && (req.getParameter("telno").length()<10) || (req.getParameter("telno").length() > 15))||
                    !functions.isValueNull(req.getParameter("telno")) ||
                    !functions.isValueNull(req.getParameter("telno")) || !ESAPI.validator().isValidInput(req.getParameter("telno"),req.getParameter("telno"), "OnlyNumber",15, false)){


                errormsg=errormsg + "Invalid Phone Number"+"<BR>";
            }


            if(!errormsg.equals(""))
            {
                req.setAttribute("error",errormsg);
                //req.setAttribute("memberid",memberid);

                redirectPage = "/memberChildList.jsp?ctoken=";
                RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            email = req.getParameter("contact_emails");
            telcc =req.getParameter("telnocc");
            telno =req.getParameter("telno");
            Hashtable updateHash = new Hashtable();
            updateHash.put("emailaddress", email);
            updateHash.put("telno",telno);
            updateHash.put("telnocc",telcc);

            int i = multipleMemberUtill.updateDetailsForMemberUserList(login,updateHash);
            if (i==1)
            {
                errormsg = login+" updated successfully";

                /*req.setAttribute("error",errormsg);
                multipleMemberUtill.viewDetailsForMemberUserList(login);
                RequestDispatcher rd1 = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
                rd1.forward(req, res);*/
            }
            else
            {
                errormsg = login+" update failed";
                /*req.setAttribute("error",errormsg);
                multipleMemberUtill.viewDetailsForMemberUserList(login);
                RequestDispatcher rd1 = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
                rd1.forward(req, res);*/
            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash",hash1);
            req.setAttribute("merchantid",memberid);
            req.setAttribute("error",errormsg);
            redirectPage = "/memberChildList.jsp?ctoken=";
        }


        if (req.getParameter("action").equalsIgnoreCase("delete"))
        {
            Hashtable hash1 = new Hashtable();
            //memberid = req.getParameter("memberid");
            String userid = req.getParameter("userid");
            int i = multipleMemberUtill.deleteDetailsForMemberUserList(login);
            int k = multipleMemberUtill.deleteUserwithModuleMapping(userid);
            if ((i==1  && k==1) || i==1)
            {
                    errormsg = login+" deleted successfully";

            }
            else
            {
                errormsg = login+" delete failed";
                //req.setAttribute("error",errormsg);
            }
            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash",hash1);
            req.setAttribute("merchantid",memberid);
            req.setAttribute("error",errormsg);
            redirectPage = "/memberChildList.jsp?ctoken=";
        }

        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(req, res);
        return;


    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
       /* inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.TELCC);*/
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }

}