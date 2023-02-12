package servlets;

import com.directi.pg.*;
import com.payment.MultiplePartnerUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import javacryption.aes.AesCtr;
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

/*
 *Created by IntelliJ IDEA.
 User: Admin
 Date: 15/4/2019
 Time: 12:41 PM
 To change this template use File | Settings | File Templates.
 */
public class NewChildPartnerSignUp extends HttpServlet
{

    private static Logger log = new Logger(NewChildPartnerSignUp.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("in side NewChildPartnerSignUp---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");
        String errormsg = "";
        String success = "";
        String passwd = null;
        String conpasswd = null;
        Hashtable detailHash = new Hashtable();
        Functions functions=new Functions();

        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
        String actionExecutorId = "0";
        String actionExecutorName="Admin"+ "-"+user.getAccountName();

        String redirectPage = "";

        if (req.getParameter("passwd") != null)
        {
            passwd = AesCtr.decrypt(req.getParameter("passwd"), req.getParameter("ctoken"), 256);
            req.setAttribute("passwd", passwd);
        }
        if (req.getParameter("conpasswd") != null)
        {
            conpasswd = AesCtr.decrypt(req.getParameter("conpasswd"), req.getParameter("ctoken"), 256);
            req.setAttribute("conpasswd", conpasswd);
        }

        if (((String) req.getParameter("step")).equals("1"))
        {
            errormsg = errormsg + validateParameters(req);
            if ((!(passwd).equals(conpasswd)))
            {
                log.debug("wrong password");
                errormsg = errormsg + "Please enter valid Password & Confirm Password.";
            }
        }

        if (errormsg != null && !errormsg.equals(""))
        {
            req.setAttribute("error", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            detailHash.put("actionExecutorId",actionExecutorId);
            detailHash.put("actionExecutorName",actionExecutorName);
            if (((String) req.getParameter("step")).equals("1"))
            {
                try
                {
                    detailHash.put("login", req.getParameter("username"));
                    detailHash.put("passwd", passwd);
                    detailHash.put("email", req.getParameter("email"));
                    detailHash.put("partnerid", req.getParameter("partnerid"));

                    if ((multiplePartnerUtill.isMember(req.getParameter("username"))) || multiplePartnerUtill.isUniqueChildMember(req.getParameter("username")))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+" already exists in records, kindly provide unique username.";
                        //System.out.println(req.getParameter("username"));
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        String Roles = functions.getRoleofPartner(req.getParameter("partnerid"));
                        if(Roles.contains("superpartner")){
                            detailHash.put("role", "childsuperpartner" );
                            req.setAttribute("role", "childsuperpartner");
                        }else{
                            detailHash.put("role", "subpartner" );
                            req.setAttribute("role", "subpartner");
                        }
                        /*req.setAttribute("role", "subpartner");
                        detailHash.put("role", "subpartner");*/
                        multiplePartnerUtill.addPartnerUser(detailHash);
                        req.setAttribute("username", (String) detailHash.get("login"));
                        success = (String) detailHash.get("login") + " Registration Successful";
                        redirectPage = "/partnerChildList.jsp?ctoken=";
                    }
                    req.setAttribute("success", success);
                    req.setAttribute("error", errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
                    rd.forward(req, res);
                }

                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error", errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (PZTechnicalViolationException tve)
                {
                    log.error("PZTechnicalViolationException ", tve);
                    if (PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION.name().equals(tve.getPzTechnicalConstraint().getPzTechEnum().name()))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username") + " already exists in records, kindly provide unique username.";
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        errormsg = errormsg + tve.getPzTechnicalConstraint().getMessage();
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                }
                catch (SystemError se)
                {
                    log.error("Leaving Partner (Step 1) throwing System Error : ", se);
                }
            }
            else if (((String) req.getParameter("step")).equals("3"))
            {
                //System.out.println("step3::::"+ req.getParameter("step") );
                detailHash = (Hashtable) session.getAttribute("childmember");
                try
                {
                    if (!ESAPI.validator().isValidInput("username", (String) req.getParameter("username"), "alphanum", 30, false))
                    {
                        req.setAttribute("username", (String) req.getParameter("username"));
                        errormsg = errormsg + "Please Enter Valid Username.";
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
                        rd.forward(req, res);
                    }
                    if ((multiplePartnerUtill.isMember(req.getParameter("username"))) || (multiplePartnerUtill.isUniqueChildMember(req.getParameter("username"))))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username") + " already exists in records, kindly provide unique username.";
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        detailHash.put("login", req.getParameter("username"));
                        String Roles = functions.getRoleofPartner(req.getParameter("partnerid"));
                        if(Roles.contains("superpartner")){
                            detailHash.put("role", "childsuperpartner" );
                            req.setAttribute("role", "childsuperpartner");
                        }else{
                            detailHash.put("role", "subpartner" );
                            req.setAttribute("role", "subpartner");
                        }
                        /*req.setAttribute("role", "subpartner");
                        detailHash.put("role", "subpartner");*/
                        multiplePartnerUtill.addPartnerUser(detailHash);
                        success = (String) detailHash.get("login") + " Registration Successful";
                        redirectPage = "/partnerChildList.jsp?ctoken=";
                    }
                    req.setAttribute("success", success);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error", errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (PZTechnicalViolationException tve)
                {
                    log.error("PZTechnicalViolationException ", tve);
                    if (PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION.name().equals(tve.getPzTechnicalConstraint().getPzTechEnum().name()))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username") + " already exists in records, kindly provide unique username.";
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        errormsg = errormsg + tve.getPzTechnicalConstraint().getMessage();
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                }
                catch (Exception e)
                {
                    log.error("Leaving Partner (Step 3) throwing Exception : ", e);
                }
            }
        }
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.USERNAME);
        inputFieldsListOptional.add(InputFields.EMAIL);
        inputFieldsListOptional.add(InputFields.PARTNERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                }
            }
        }
        return error;
    }
}

