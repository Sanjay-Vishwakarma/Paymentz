package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.payment.MultipleMemberUtill;
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
 * Created by Jinesh on 1/5/2016.
 */
public class NewChildMemberSignUp extends HttpServlet
{
    private static Logger log = new Logger(NewChildMemberSignUp.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("in side NewChildMemberSignUp---");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String NewChildMemberSignUp_Password_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Password_errormsg")) ? rb1.getString("NewChildMemberSignUp_Password_errormsg") : "Invalid Password";
        String NewChildMemberSignUp_Partnerid_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Partnerid_errormsg")) ? rb1.getString("NewChildMemberSignUp_Partnerid_errormsg") : "Invalid Partner ID";
        String NewChildMemberSignUp_Partner_Member_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Partner_Member_errormsg")) ? rb1.getString("NewChildMemberSignUp_Partner_Member_errormsg") : "Invalid Partner Member Mapping";
        String NewChildMemberSignUp_Partner_Mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Partner_Mapping_errormsg")) ? rb1.getString("NewChildMemberSignUp_Partner_Mapping_errormsg") : "Invalid Partner Mapping";
        String NewChildMemberSignUp_Please_Mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Please_Mapping_errormsg")) ? rb1.getString("NewChildMemberSignUp_Please_Mapping_errormsg") : "Please enter valid Password & Confirm Password.";
        String NewChildMemberSignUp_already_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_already_errormsg")) ? rb1.getString("NewChildMemberSignUp_already_errormsg") : "already exists in records, kindly provide unique username.";
        String NewChildMemberSignUp_Registration_errormsg = StringUtils.isNotEmpty(rb1.getString("NewChildMemberSignUp_Registration_errormsg")) ? rb1.getString("NewChildMemberSignUp_Registration_errormsg") : "Registration Successful";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        res.setContentType("text/html");
        String errormsg = "";
        String successMsg = "";
        String passwd=null;
        String conpasswd=null;
        Hashtable detailHash = new Hashtable();
        Merchants merchants = new Merchants();
        Functions functions=new Functions();
        String role =(String)session.getAttribute("role");
        String username =(String)session.getAttribute("username");
        String actionExecutorId = (String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        String redirectPage="";

        detailHash.put("actionExecutorId",actionExecutorId);
        detailHash.put("actionExecutorName",actionExecutorName);
        if(req.getParameter("passwd") != null && req.getParameter("passwd").contains("<")){
            req.setAttribute("error",NewChildMemberSignUp_Password_errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken="+user.getCSRFToken());
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

        String EOL = "</BR>";
        Functions function = new Functions();
        String pid = "";
        if(((String) req.getParameter("step")).equals("1"))
        {
            if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
            {
                errormsg =  NewChildMemberSignUp_Partnerid_errormsg+ EOL;
            }
            else if(function.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMapped(req.getParameter("pid"),(String)session.getAttribute("merchantid")))
            {
                try
                {
                    pid = req.getParameter("pid");
                    if (function.isValueNull(req.getParameter("merchantid")) && !partner.isPartnerMemberMapped(req.getParameter("merchantid"), pid))
                    {
                        errormsg =  NewChildMemberSignUp_Partner_Member_errormsg+ EOL;
                    }
                }catch(Exception e){
                    log.error("Exception---" + e);
                }
            }else if(!function.isValueNull(req.getParameter("pid")))
            {
                pid= (String)session.getAttribute("merchantid") ;
                if (function.isValueNull(req.getParameter("merchantid")) && !partner.isPartnerSuperpartnerMembersMapped(req.getParameter("merchantid"), (String)session.getAttribute("merchantid")))
                {
                   errormsg =NewChildMemberSignUp_Partner_Member_errormsg + EOL;
                }
            }
            else {
                errormsg = NewChildMemberSignUp_Partner_Mapping_errormsg + EOL;
            }
            errormsg=errormsg+validateParameters(req);
            if ((!(passwd).equals(conpasswd)))
            {
                log.debug("wrong password");
                //msg="X";
                errormsg = errormsg + NewChildMemberSignUp_Please_Mapping_errormsg;
            }

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

        }

        if(errormsg!=null && !errormsg.equals(""))
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken="+user.getCSRFToken());
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
                    detailHash.put("merchantid", req.getParameter("merchantid"));
                    detailHash.put("email", req.getParameter("email"));
                    detailHash.put("telno",req.getParameter("telno"));
                    detailHash.put("telnocc",req.getParameter("telnocc"));
                    detailHash.put("partnerid", pid);
                    detailHash.put("company", session.getAttribute("partnername"));

                    if ((merchants.isMember(req.getParameter("username"))) || multipleMemberUtill.isUniqueChildMember(req.getParameter("username")))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildMemberSignUp_already_errormsg;
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        req.setAttribute("role","submerchant");
                        multipleMemberUtill.addMerchantUser(detailHash);
                        req.setAttribute("username", (String) detailHash.get("login"));
                        successMsg=(String) detailHash.get("login")+NewChildMemberSignUp_Registration_errormsg;
                        redirectPage="/memberChildList.jsp?ctoken=";
                    }
                    req.setAttribute("error",errormsg);
                    req.setAttribute("success",successMsg);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                    rd.forward(req, res);
                }

                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error",errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken="+user.getCSRFToken());
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
                        errormsg = req.getParameter("username")+NewChildMemberSignUp_already_errormsg;
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        errormsg = errormsg + tve.getPzTechnicalConstraint().getMessage();
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
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
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                        rd.forward(req, res);
                    }
                    if ((merchants.isMember(req.getParameter("username"))) || (multipleMemberUtill.isUniqueChildMember(req.getParameter("username"))))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+NewChildMemberSignUp_already_errormsg;
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        detailHash.put("login", req.getParameter("username"));
                        req.setAttribute("role","submerchant");
                        multipleMemberUtill.addMerchantUser(detailHash);
                        successMsg=(String) detailHash.get("login")+NewChildMemberSignUp_Registration_errormsg;
                        redirectPage="/memberChildList.jsp?ctoken=";
                    }
                    req.setAttribute("error",errormsg);
                    req.setAttribute("success",successMsg);
                    RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("DB Connection error ", dbe);
                    errormsg = "Internal error occurred:::Please contact your Admin";
                    req.setAttribute("error",errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken="+user.getCSRFToken());
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
                        errormsg = req.getParameter("username")+NewChildMemberSignUp_already_errormsg;
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                        req.setAttribute("error",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher(redirectPage+user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    else
                    {
                        errormsg = errormsg + tve.getPzTechnicalConstraint().getMessage();
                        req.setAttribute("error", errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                }
                catch (Exception e)
                {
                    log.error("Leaving Merchants (Step 3) throwing Exception : ",e);
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

        inputFieldsListOptional.add(InputFields.MERCHANTID);
        inputFieldsListOptional.add(InputFields.USERNAME);
        //inputFieldsListOptional.add(InputFields.PASSWORD);
        inputFieldsListOptional.add(InputFields.EMAIL);
       /* inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.TELCC);*/

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
    }
}