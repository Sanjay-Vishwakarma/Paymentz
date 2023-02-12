package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kanchan on 29-01-2021.
 */
public class EditAgentDetails extends HttpServlet
{
    private static Logger log = new Logger(EditAgentDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PartnerFunctions partner= new PartnerFunctions();
        log.debug("Entering EditAgent Details....");
        HttpSession session= request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!partner.isLoggedInPartner(session))
        {   log.debug("Partner is logout");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        boolean flag= true;
        String EOL = "<BR>";
        String errormsg= "<center><font class=\"text\" face=\"arial\"><b>"+"Following information are incorrect:-"+EOL+"</b></font></center>";
        String error= "";
        String agentid="";
        String action= "";
        String name="";
        String emailid="";
        String support_url="";
        String notifyemail="";
        String country="";
        String phno="";
        String siteurl="";
        String username = "";
        String passwd = "";
        String conpasswd = "";
        String company_name = "";
        String partnerid="";

        String mainContact_cCmailId="";
        String mainContact_phone="";
        String refundContact_name="";
        String refundContact_mailId="";
        String refundContact_cCmailId="";
        String refundContact_phone="";

        String cbContact_name="";
        String cbContact_mailId="";
        String cbContact_cCmailId="";
        String cbContact_phone="";
        String salesContact_name="";
        String salesContact_mailid="";
        String salesContact_cCmailId="";
        String salesContact_phone="";

        String billingContact_name="";
        String billingContact_mailId="";
        String billingContact_cCmailId="";
        String billingContact_phone="";

        String fraudContact_name="";
        String fraudContact_mailid="";
        String fraudContact_cCmailId="";
        String fraudContact_phone="";

        String technicalContact_name="";
        String technicalContact_mailId="";
        String technicalContact_cCmailId="";
        String technicalContact_phone="";
        String emailTemplateLang="";

        String partnerId="";
        String isipwhitelist= request.getParameter("isipwhitelisted");
        String isReadOnly="modify";

        error = validateMandatoryParameters(request);
        errormsg = errormsg + error;
        if (!error.equals(""))
        {
            flag = false;
        }

        String error1="";
        error1 = validateOptionalParameters(request);
        errormsg = errormsg + error1;
        if (!error1.equals(""))
        {
            flag = false;
        }

            else
            {
                name = request.getParameter("contact_persons");
                emailid = request.getParameter("contact_emails");
                siteurl = request.getParameter("sitename");
                support_url = request.getParameter("supporturl");
                notifyemail = request.getParameter("notifyemail");
                country = request.getParameter("country");
                phno = request.getParameter("telno");
                partnerId = request.getParameter("pid");
                agentid = request.getParameter("agentid");
                action= request.getParameter("action");

                mainContact_cCmailId = request.getParameter("maincontact_ccmailid");
                mainContact_phone = request.getParameter("maincontact_phone");

                cbContact_name = request.getParameter("cbcontact_name");
                cbContact_mailId = request.getParameter("cbcontact_mailid");
                cbContact_cCmailId = request.getParameter("cbcontact_ccmailid");
                cbContact_phone = request.getParameter("cbcontact_phone");

                refundContact_name = request.getParameter("refundcontact_name");
                refundContact_mailId = request.getParameter("refundcontact_mailid");
                refundContact_cCmailId = request.getParameter("refundcontact_ccmailid");
                refundContact_phone = request.getParameter("refundcontact_phone");

                salesContact_name = request.getParameter("salescontact_name");
                salesContact_mailid = request.getParameter("salescontact_mailid");
                salesContact_cCmailId = request.getParameter("salescontact_ccmailid");
                salesContact_phone = request.getParameter("salescontact_phone");

                fraudContact_name = request.getParameter("fraudcontact_name");
                fraudContact_mailid = request.getParameter("fraudcontact_mailid");
                fraudContact_cCmailId = request.getParameter("fraudcontact_ccmailid");
                fraudContact_phone = request.getParameter("fraudcontact_phone");

                technicalContact_name = request.getParameter("technicalcontact_name");
                technicalContact_mailId = request.getParameter("technicalcontact_mailid");
                technicalContact_cCmailId = request.getParameter("technicalcontact_ccmailid");
                technicalContact_phone = request.getParameter("technicalcontact_phone");

                billingContact_name = request.getParameter("billingcontact_name");
                billingContact_mailId = request.getParameter("billingcontact_mailid");
                billingContact_cCmailId = request.getParameter("billingcontact_ccmailid");
                billingContact_phone = request.getParameter("billingcontact_phone");
                emailTemplateLang = request.getParameter("emailTemplateLang");
                Functions functions = new Functions();

                if (!ESAPI.validator().isValidInput("sitename",request.getParameter("sitename"),"URL",100,false) || functions.hasHTMLTags(request.getParameter("sitename")))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Site URL."+ EOL;
                }
                if (!ESAPI.validator().isValidInput("supporturl",request.getParameter("supporturl"),"URL",255,false) || functions.hasHTMLTags(request.getParameter("supporturl")))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Support URL."+ EOL;
                }
                if (!ESAPI.validator().isValidInput("telno",request.getParameter("telno"),"SignupPhone",20,false))
                {
                    flag= false;
                    errormsg = errormsg + "Invalid Support Number."+EOL;
                }
                if (!ESAPI.validator().isValidInput("notifyemail",request.getParameter("notifyemail"),"Email",100,false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid  Notify Email Id." + EOL;
                }
                if (!ESAPI.validator().isValidInput("contact_emails",request.getParameter("contact_emails"),"Email",100,false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Main Contact Email. "+ EOL;
                }
                if (!ESAPI.validator().isValidInput("contact_persons",request.getParameter("contact_persons"),"contactName",100,false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Main Contact." + EOL;
                }
            }

            if (action.equalsIgnoreCase("modify"))
            {
                  isReadOnly="modify";
            }
            if (flag == true)
            {
                String errorMessage = "";
                Connection con = null;
                PreparedStatement ps = null;
                String str = "select * from partners";
                String count = "select count(*) from partners";

                String query = "Update agents set contact_persons=?,contact_emails=?,siteurl=?,supporturl=?,notifyemail=?,maincontact_ccmailid=?,maincontact_phone=?,cbcontact_name=?,cbcontact_mailid=?,cbcontact_ccmailid=?,cbcontact_phone=?,refundcontact_name=?,refundcontact_mailid=?,refundcontact_ccmailid=?,refundcontact_phone=?,salescontact_name=?,salesemail=?,salescontact_ccmailid=?,salescontact_phone=?,fraudcontact_name=?,fraudcontact_mailid=?,fraudcontact_ccmailid=?,fraudcontact_phone=?,technicalcontact_name=?,technicalcontact_mailid=?,technicalcontact_ccmailid=?,technicalcontact_phone=?,billingcontact_ccmailid=?,billingcontact_phone=?,billingcontact_name=?,billingemail=?,country=?,telno=?,partnerId=?,isIpWhitelisted=?,emailTemplateLang=? where agentId=?";
                try
                {
                    con = Database.getConnection();
                    ps = con.prepareStatement(query);
                    ps.setString(1, name);
                    ps.setString(2, emailid);
                    ps.setString(3, siteurl);
                    ps.setString(4, support_url);
                    ps.setString(5, notifyemail);

                    ps.setString(6, mainContact_cCmailId);
                    ps.setString(7, mainContact_phone);
                    ps.setString(8, cbContact_name);
                    ps.setString(9, cbContact_mailId);
                    ps.setString(10, cbContact_cCmailId);
                    ps.setString(11, cbContact_phone);

                    ps.setString(12, refundContact_name);
                    ps.setString(13, refundContact_mailId);
                    ps.setString(14, refundContact_cCmailId);
                    ps.setString(15, refundContact_phone);

                    ps.setString(16, salesContact_name);
                    ps.setString(17, salesContact_mailid);
                    ps.setString(18, salesContact_cCmailId);
                    ps.setString(19, salesContact_phone);

                    ps.setString(20, fraudContact_name);
                    ps.setString(21, fraudContact_mailid);
                    ps.setString(22, fraudContact_cCmailId);
                    ps.setString(23, fraudContact_phone);

                    ps.setString(24, technicalContact_name);
                    ps.setString(25, technicalContact_mailId);
                    ps.setString(26, technicalContact_cCmailId);
                    ps.setString(27, technicalContact_phone);

                    ps.setString(28, billingContact_cCmailId);
                    ps.setString(29, billingContact_phone);
                    ps.setString(30, billingContact_name);
                    ps.setString(31, billingContact_mailId);

                    ps.setString(32, country);
                    ps.setString(33, phno);
                    ps.setString(34, partnerId);
                    ps.setString(35, isipwhitelist);
                    ps.setString(36, emailTemplateLang);
                    ps.setString(37, agentid);
                    log.error("QUERY result: " + query);
                    int i = ps.executeUpdate();
                    log.error("QUERY result: " + query);

                    if (i!= 0)
                    {
                        errorMessage = "Record updated successfully";
                    }
                }
                catch (SystemError systemError)
                {
                    log.error("Sql Exception :::::", systemError);
                }
                catch (SQLException e)
                {
                    log.error("Exception while updating  : " + e.getMessage());
                }
                finally
                {
                    Database.closePreparedStatement(ps);
                    Database.closeConnection(con);
                }
                request.setAttribute("message", errorMessage);
                RequestDispatcher rd = request.getRequestDispatcher("/agentInterface.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
            }
            else
            {
                request.setAttribute("message", errormsg);
                request.setAttribute("isreadonly",isReadOnly);
                request.setAttribute("action",action);
                RequestDispatcher rd = request.getRequestDispatcher("/viewAgentDetails.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
            }
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        String error="";
        String EOL="<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.COUNTRY);
        inputFieldsListOptional.add(InputFields.PID);
        inputFieldsListOptional.add(InputFields.AGENTID);
        inputFieldsListOptional.add(InputFields.ACTION);

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
        return  error;
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error1 = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.MAINCONTACT_CC);
        inputFieldsListOptional.add(InputFields.MAINCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.CBCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.CBCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.CBCONTACT_CC);
        inputFieldsListOptional.add(InputFields.CBCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_CC);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_CC);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_CC);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_CC);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_CC);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_PHONE);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error1 = error1+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error1;
    }

}
