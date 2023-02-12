package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.payment.MultiplePartnerUtill;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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

/*
 *Created by IntelliJ IDEA.
 User: Sanjeet
 Date: 15/4/2019
 Time: 12:41 PM
 To change this template use File | Settings | File Templates.
 */
public class EditPartnerUserList extends HttpServlet
{

    private static Logger log = new Logger(EditMemberUserList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("inside EditPartnerUserList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        res.setContentType("text/html");

        String action = "";
        String login = req.getParameter("login");
        String partnerid = (String) session.getAttribute("partnerid");
        String errormsg = "";
        String success = "";
        String email = "";
        Hashtable hash = new Hashtable();
        String redirectPage = "";
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();

        //System.out.println("action::::"+req.getParameter("action"));
        if (req.getParameter("action").equalsIgnoreCase("view") || req.getParameter("action").equalsIgnoreCase("modify1"))
        {
            hash = multiplePartnerUtill.viewDetailsForPartnerUserList(login);

            req.setAttribute("detailHash",hash);
            req.setAttribute("login",login);
            req.setAttribute("action",req.getParameter("action"));
            req.setAttribute("partnerid",hash.get("partnerid"));
            req.setAttribute("error",errormsg);
            redirectPage = "/viewPartnerUserList.jsp?ctoken=";
        }

        if (req.getParameter("action").equalsIgnoreCase("modify"))
        {
            Hashtable hash1 = new Hashtable();

            errormsg = errormsg+validateMandatoryParameters(req);
            if(!errormsg.equals(""))
            {
                req.setAttribute("error",errormsg);
                redirectPage = "/partnerChildList.jsp?ctoken=";
                RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            email = req.getParameter("contact_emails");
            Hashtable updateHash = new Hashtable();
            updateHash.put("emailaddress", email);

            int i = multiplePartnerUtill.updateDetailsForPartnerUserList(login, updateHash);
            if (i==1)
            {
                success = login+" Updated Successfully";
            }
            else
            {
                errormsg = login+" Update Failed";
            }

            hash1 = multiplePartnerUtill.getDetailsForSubpartner(partnerid);
            req.setAttribute("detailHash",hash1);
            req.setAttribute("partnerid",partnerid);
            req.setAttribute("error",errormsg);
            req.setAttribute("success",success);
            redirectPage = "/partnerChildList.jsp?ctoken=";
        }

        if (req.getParameter("action").equalsIgnoreCase("delete"))
        {
            Hashtable hash1 = new Hashtable();
            partnerid = req.getParameter("partnerid");
            int i = multiplePartnerUtill.deleteDetailsForPartnerUserList(login);
            if (i==1)
            {
                success = login+" deleted successfully";
            }
            else
            {
                errormsg = login+" delete failed";
            }

            hash1 = multiplePartnerUtill.getDetailsForSubpartner(partnerid);
            req.setAttribute("detailHash",hash1);
            req.setAttribute("partnerid",partnerid);
            req.setAttribute("success",success);
            req.setAttribute("error",errormsg);
            redirectPage = "/partnerChildList.jsp?ctoken=";
        }

        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
        rd.forward(req, res);
        return;

    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}
