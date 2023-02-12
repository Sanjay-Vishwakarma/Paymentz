package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.MultiplePartnerUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import javacryption.aes.AesCtr;
import org.apache.commons.lang.StringUtils;
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
import java.util.ResourceBundle;


/**
 * Created by Kajal on 1/5/2016.
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
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String NewChildPartnerSignup_PartnerID_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildPartnerSignup_PartnerID_errormsg")) ? rb1.getString("NewChildPartnerSignup_PartnerID_errormsg") : "Invalid Partner ID";
        String NewChildPartnerSignup_Partner_mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildPartnerSignup_Partner_mapping_errormsg")) ? rb1.getString("NewChildPartnerSignup_Partner_mapping_errormsg") : "Invalid Partner Mapping";
        String NewChildPartnerSignup_please = StringUtils.isNotEmpty(rb1.getString("NewChildPartnerSignup_please")) ? rb1.getString("NewChildPartnerSignup_please") : "Please enter valid Password & Confirm Password.";
        String NewChildPartnerSignup_already_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildPartnerSignup_already_errormsg")) ? rb1.getString("NewChildPartnerSignup_already_errormsg") : "already exists in records, kindly provide unique username.";
        String NewChildPartnerSignup_Registration_Successful_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildPartnerSignup_Registration_Successful_errormsg")) ? rb1.getString("NewChildPartnerSignup_Registration_Successful_errormsg") : "Registration Successful";


        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        res.setContentType("text/html");
        String errormsg = "";
        String success = "";
        String passwd=null;
        String conpasswd=null;
        String EOL = "<BR>";
        String role =(String)session.getAttribute("role");
        String username =(String)session.getAttribute("username");
        String actionExecutorId = (String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
  Hashtable detailHash = new Hashtable();
        //Merchants merchants = new Merchants();
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();

        String redirectPage="";

        detailHash.put("actionExecutorId",actionExecutorId);
        detailHash.put("actionExecutorName",actionExecutorName);
            if(req.getParameter("passwd") != null && req.getParameter("passwd").contains("<")){
                req.setAttribute("error","Invalid Password");
                RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        else
            {
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
            }

        Functions function = new Functions();
        String pid ="";
        if(((String) req.getParameter("step")).equals("1"))
        {
            if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, false))
            {
                errormsg = NewChildPartnerSignup_PartnerID_errormsg + EOL;
            }
            else if(function.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMapped(req.getParameter("pid"),(String)session.getAttribute("merchantid")))
            {
                pid = req.getParameter("pid");
            }else{
                errormsg =  NewChildPartnerSignup_Partner_mapping_errormsg+ EOL;
            }
            errormsg=errormsg+validateParameters(req);
            if ((!(passwd).equals(conpasswd)))
            {
                errormsg = errormsg + NewChildPartnerSignup_please;
            }
        }

        if(errormsg!=null && !errormsg.equals(""))
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            if (((String) req.getParameter("step")).equals("1"))
            {
                try
                {
                    detailHash.put("login", req.getParameter("username"));
                    detailHash.put("passwd", passwd);
                    detailHash.put("email", req.getParameter("email"));
                    detailHash.put("partnerid", pid );

                    if ((multiplePartnerUtill.isMember(req.getParameter("username"))) || multiplePartnerUtill.isUniqueChildMember(req.getParameter("username")))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildPartnerSignup_already_errormsg;
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        String Roles = partner.getRoleofPartner(pid);
                        if(Roles.contains("superpartner")){
                            detailHash.put("role", "childsuperpartner" );
                            req.setAttribute("role", "childsuperpartner");
                        }else{
                            detailHash.put("role", "subpartner" );
                            req.setAttribute("role", "subpartner");
                        }
                        multiplePartnerUtill.addPartnerUser(detailHash);
                        req.setAttribute("username", (String) detailHash.get("login"));
                        success=(String) detailHash.get("login")+NewChildPartnerSignup_Registration_Successful_errormsg;
                        redirectPage="/partnerChildList.jsp?ctoken=";
                    }
                    req.setAttribute("success",success);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                    rd.forward(req, res);
                }

                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error",errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (PZTechnicalViolationException tve)
                {
                    log.error("PZTechnicalViolationException ",tve);
                    if(PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION.name().equals(tve.getPzTechnicalConstraint().getPzTechEnum().name()))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildPartnerSignup_already_errormsg;
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
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
                    log.error("Leaving Partner (Step 3) throwing System Error : ",se);
                }
            }
            else if (((String) req.getParameter("step")).equals("3"))
            {
                detailHash = (Hashtable) session.getAttribute("childmember");
                //String userName =  (String) session.getAttribute("username");
                try
                {
                    if (!ESAPI.validator().isValidInput("username",(String) req.getParameter("username"),"alphanum",30,false))
                    {
                        req.setAttribute("username", (String) req.getParameter("username"));
                        errormsg = errormsg + "Please Enter Valid Username.";
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                        rd.forward(req, res);
                    }
                    if ((multiplePartnerUtill.isMember(req.getParameter("username"))) || (multiplePartnerUtill.isUniqueChildMember(req.getParameter("username"))))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildPartnerSignup_already_errormsg;
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        detailHash.put("login", req.getParameter("username"));
                        String Roles = partner.getRoleofPartner((String) detailHash.get("partnerid"));
                        if(Roles.contains("superpartner")){
                            detailHash.put("role", "childsuperpartner");
                            req.setAttribute("role", "childsuperpartner");
                        }else{
                            detailHash.put("role", "subpartner");
                            req.setAttribute("role", "subpartner");
                        }
                        multiplePartnerUtill.addPartnerUser(detailHash);
                        success=(String) detailHash.get("login")+NewChildPartnerSignup_Registration_Successful_errormsg;
                        redirectPage="/partnerChildList.jsp?ctoken=";
                    }
                    req.setAttribute("success",success);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error",errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/partnerChildSignup.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (PZTechnicalViolationException tve)
                {
                    log.error("PZTechnicalViolationException ",tve);
                    if(PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION.name().equals(tve.getPzTechnicalConstraint().getPzTechEnum().name()))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildPartnerSignup_already_errormsg;
                        redirectPage = "/newPartnerChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
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
                    log.error("Leaving Partner (Step 3) throwing Exception : ",e);
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

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,false);

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