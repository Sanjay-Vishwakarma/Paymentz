package servlets;
import com.directi.pg.*;
import com.payment.MultipleMemberUtill;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.*;



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
        Functions functions=new Functions();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        res.setContentType("text/html");


        String action = "";
        String login = req.getParameter("login");
        String memberid = req.getParameter("memberid");
        String errormsg = "";
        String email = "";
        String telno="";
        String telcc="";
        Hashtable hash = new Hashtable();
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
            memberid = req.getParameter("memberid");

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
            telno=req.getParameter("telno");
            telcc=req.getParameter("telnocc");
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
            req.setAttribute("memberid",memberid);
            req.setAttribute("error",errormsg);
            redirectPage = "/memberChildList.jsp?ctoken=";
        }


        if (req.getParameter("action").equalsIgnoreCase("delete"))
        {
            Hashtable hash1 = new Hashtable();
            memberid = req.getParameter("memberid");
            int i = multipleMemberUtill.deleteDetailsForMemberUserList(login);
            if (i==1)
            {
                errormsg = login+" deleted successfully";
                //req.setAttribute("error",errormsg);
            }
            else
            {
                errormsg = login+" delete failed";
                //req.setAttribute("error",errormsg);
            }

            hash1 = multipleMemberUtill.getDetailsForSubmerchant(memberid);
            req.setAttribute("detailHash",hash1);
            req.setAttribute("memberid",memberid);
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
      /*  inputFieldsListOptional.add(InputFields.TELNO);
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
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

}
