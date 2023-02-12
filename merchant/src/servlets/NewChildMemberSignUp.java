import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.payment.MultipleMemberUtill;
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
        String EOL = "<BR>";

        Merchants merchants = new Merchants();
        Functions functions=new Functions();
        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        res.setContentType("text/html");
        String errormsg = "";
        String passwd=null;
        String conpasswd=null;
        String memberid= req.getParameter("merchantid");
        String telno= req.getParameter("telno");
        String telcc= req.getParameter("telnocc");
        Hashtable detailHash = new Hashtable();
        //Merchants merchants = new Merchants();
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        String redirectPage="";
        if(req.getParameter("username")!=null)
        {
            if (!ESAPI.validator().isValidInput("username", req.getParameter("username"), "Name", 50, false))
            {
                errormsg=("Invalid  UserName" + EOL);
            }
        }
        if(errormsg.length() > 0)
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildSignup.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(req.getParameter("passwd").contains("<")){
            req.setAttribute("error","Invalid Password");
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

        if(((String) req.getParameter("step")).equals("1"))
        {
            errormsg=errormsg+validateParameters(req);
            if ((!(passwd).equals(conpasswd)))
            {
                log.debug("wrong password");
                //msg="X";
                errormsg = errormsg + "Please enter valid Password & Confirm Password."+EOL;
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
                    detailHash.put("merchantid", memberid);
                    detailHash.put("telno", telno);
                    detailHash.put("telnocc", telcc);
                    detailHash.put("email", req.getParameter("email"));
                    detailHash.put("partnerid", session.getAttribute("partnerid"));
                    detailHash.put("company", session.getAttribute("company"));

                    if ((merchants.isMember(req.getParameter("username"))) ||multipleMemberUtill.isUniqueChildMember(req.getParameter("username")))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+" already exists in records, kindly provide unique username.";
                        redirectPage = "/memberChildSignup.jsp?ctoken=";
                    }
                    else
                    {
                        req.setAttribute("role","submerchant");
                        multipleMemberUtill.addMerchantUser(detailHash);
                        req.setAttribute("username", (String) detailHash.get("login"));
                        errormsg=(String) detailHash.get("login")+" Registration successful";
                        redirectPage="/memberChildList.jsp?ctoken=";
                    }
                    req.setAttribute("error",errormsg);
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
                        errormsg = req.getParameter("username")+" already exists in records, kindly provide unique username.";
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
                    if (!ESAPI.validator().isValidInput("username",(String) req.getParameter("username"),"Name",30,false))
                    {
                        req.setAttribute("username", (String) req.getParameter("username"));
                        errormsg = errormsg + "Please Enter Valid Username.";
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                    }
                    if ((merchants.isMember(req.getParameter("username"))) || (multipleMemberUtill.isUniqueChildMember(req.getParameter("username"))))
                    {
                        log.debug("redirect to NEWLOGIN");
                        session.setAttribute("childmember", detailHash);
                        req.setAttribute("username", req.getParameter("username"));
                        errormsg = req.getParameter("username")+" already exists in records, kindly provide unique username.";
                        redirectPage = "/newChildLoginName.jsp?ctoken=";
                    }
                    else
                    {
                        detailHash.put("login", req.getParameter("username"));
                        req.setAttribute("role","submerchant");
                        multipleMemberUtill.addMerchantUser(detailHash);
                        errormsg=(String) detailHash.get("login")+" Registration successful";
                        redirectPage="/memberChildList.jsp?ctoken=";
                    }
                    req.setAttribute("error",errormsg);
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
                        errormsg = req.getParameter("username")+" already exists in records, kindly provide unique username.";
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

        inputFieldsListOptional.add(InputFields.USERNAME);
        //inputFieldsListOptional.add(InputFields.PASSWORD);
        inputFieldsListOptional.add(InputFields.MERCHANTID);
        inputFieldsListOptional.add(InputFields.EMAIL);
      /*  inputFieldsListOptional.add(InputFields.TELNO);
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
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()).concat(EOL);
                }
            }
        }
        return error;
    }
}
