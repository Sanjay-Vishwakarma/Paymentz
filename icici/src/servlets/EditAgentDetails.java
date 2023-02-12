import com.directi.pg.*;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 2/14/14
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditAgentDetails extends HttpServlet
{
    private static Logger log = new Logger(EditAgentDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in  EditAgentDetails");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        boolean flag = true;
        String EOL = "<BR>";
        String errormsg = "<center><font class=\"text\" face=\"arial\"><b>"+"Following information are incorrect:-"+EOL+"</b></font></center>";
        String error ="";
        String agentid="";
        String action = "";
        String name ="";
        String emailid ="";
        String support_url ="";
        String notifyemail ="";
        String country ="";
        String phno ="";
        String siteurl = "";
        String username = "";
        String passwd = "";
        String conpasswd = "";
        String company_name = "";
        String sitename = "";

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

        String telno = "";
        String logo = "";
        String emailTemplateLang="";

        String partnerId="";
        String isipwhitelist = req.getParameter("isipwhitelisted");
        error = validateMandatoryParameters(req);
        errormsg = errormsg + error;
        if (!error.equals(""))
            flag = false;

        error = validateOptionalParameters(req);
        errormsg = errormsg + error;
        if (!error.equals(""))
            flag = false;

        else
        {
            name = req.getParameter("contact_persons");
            emailid = req.getParameter("contact_emails");
            siteurl = req.getParameter("sitename");
            support_url = req.getParameter("supporturl");
            notifyemail = req.getParameter("notifyemail");
            country = req.getParameter("country");
            phno = req.getParameter("telno");
            partnerId = req.getParameter("partnerId");
            agentid = req.getParameter("agentid");
            action = req.getParameter("action");

            mainContact_cCmailId=req.getParameter("maincontact_ccmailid");
            mainContact_phone=req.getParameter("maincontact_phone");

            cbContact_name=req.getParameter("cbcontact_name");
            cbContact_mailId=req.getParameter("cbcontact_mailid");
            cbContact_cCmailId=req.getParameter("cbcontact_ccmailid");
            cbContact_phone=req.getParameter("cbcontact_phone");

            refundContact_name=req.getParameter("refundcontact_name");
            refundContact_mailId=req.getParameter("refundcontact_mailid");
            refundContact_cCmailId=req.getParameter("refundcontact_ccmailid");
            refundContact_phone=req.getParameter("refundcontact_phone");

            salesContact_name=req.getParameter("salescontact_name");
            salesContact_mailid=req.getParameter("salescontact_mailid");
            salesContact_cCmailId=req.getParameter("salescontact_ccmailid");
            salesContact_phone=req.getParameter("salescontact_phone");

            fraudContact_name=req.getParameter("fraudcontact_name");
            fraudContact_mailid=req.getParameter("fraudcontact_mailid");
            fraudContact_cCmailId=req.getParameter("fraudcontact_ccmailid");
            fraudContact_phone=req.getParameter("fraudcontact_phone");

            technicalContact_name=req.getParameter("technicalcontact_name");
            technicalContact_mailId=req.getParameter("technicalcontact_mailid");
            technicalContact_cCmailId=req.getParameter("technicalcontact_ccmailid");
            technicalContact_phone=req.getParameter("technicalcontact_phone");

            billingContact_name=req.getParameter("billingcontact_name");
            billingContact_mailId=req.getParameter("billingcontact_mailid");
            billingContact_cCmailId=req.getParameter("billingcontact_ccmailid");
            billingContact_phone=req.getParameter("billingcontact_phone");
            emailTemplateLang=req.getParameter("emailTemplateLang");
        }


        if(flag == true)
        {
            String errorMessage="";
            Connection conn = null;
            PreparedStatement ps = null;
            String str="select * from partners";
            String count="select count(*) from partners";

            String query = "UPDATE agents SET contact_persons=?,contact_emails=?,siteurl=?,supporturl=?,notifyemail=?,maincontact_ccmailid=?,maincontact_phone=?,cbcontact_name=?,cbcontact_mailid=?,cbcontact_ccmailid=?,cbcontact_phone=?,refundcontact_name=?,refundcontact_mailid=?,refundcontact_ccmailid=?,refundcontact_phone=?,salescontact_name=?,salesemail=?,salescontact_ccmailid=?,salescontact_phone=?,fraudcontact_name=?,fraudcontact_mailid=?,fraudcontact_ccmailid=?,fraudcontact_phone=?,technicalcontact_name=?,technicalcontact_mailid=?,technicalcontact_ccmailid=?,technicalcontact_phone=?,billingcontact_ccmailid=?,billingcontact_phone=?,billingcontact_name=?,billingemail=?,country=?,telno=?,partnerId=?,isIpWhitelisted=?,emailTemplateLang=?  WHERE agentId=?";
            try
            {
                conn = Database.getConnection();
                ps = conn.prepareStatement(query);
                ps.setString(1,name);
                ps.setString(2,emailid);
                ps.setString(3,siteurl);
                ps.setString(4,support_url);
                ps.setString(5,notifyemail);

                ps.setString(6,mainContact_cCmailId);
                ps.setString(7,mainContact_phone);

                ps.setString(8,cbContact_name);
                ps.setString(9,cbContact_mailId);
                ps.setString(10,cbContact_cCmailId);
                ps.setString(11,cbContact_phone);

                ps.setString(12,refundContact_name);
                ps.setString(13,refundContact_mailId);
                ps.setString(14,refundContact_cCmailId);
                ps.setString(15,refundContact_phone);

                ps.setString(16,salesContact_name);
                ps.setString(17,salesContact_mailid);
                ps.setString(18,salesContact_cCmailId);
                ps.setString(19,salesContact_phone);

                ps.setString(20,fraudContact_name);
                ps.setString(21,fraudContact_mailid);
                ps.setString(22,fraudContact_cCmailId);
                ps.setString(23,fraudContact_phone);

                ps.setString(24,technicalContact_name);
                ps.setString(25,technicalContact_mailId);
                ps.setString(26,technicalContact_cCmailId);
                ps.setString(27,technicalContact_phone);

                ps.setString(28,billingContact_cCmailId);
                ps.setString(29,billingContact_phone);
                ps.setString(30,billingContact_name);
                ps.setString(31,billingContact_mailId);

                ps.setString(32,country);
                ps.setString(33,phno);
                ps.setString(34,partnerId);
                ps.setString(35,isipwhitelist);
                ps.setString(36,emailTemplateLang);
                ps.setString(37,agentid);
                log.debug("QUERYresult " + query);
                int i =ps.executeUpdate();
                log.debug("QUERYresult " + query);


                if(i!=0)
                {
                    errorMessage="<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Record Updated Successfully"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                }
                else
                {
                    errorMessage="<font class=\"text\" face=\"arial\">"+"<center>"+"<b>"+"Update Failed"+"</b>"+"</center>"+"</font>"+"<BR><BR>";
                }
            }
            catch (SystemError systemError)
            {
                log.error("Sql Exception :::::",systemError);

            }
            catch (SQLException e)
            {
                log.error("Exception while updating  : " + e.getMessage());
            }
            finally
            {
                Database.closePreparedStatement(ps);
                Database.closeConnection(conn);
            }

            req.setAttribute("message",errorMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {

            req.setAttribute("message",errormsg);

            RequestDispatcher rd = req.getRequestDispatcher("/servlet/ViewAgentDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);
        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
        inputFieldsListOptional.add(InputFields.COUNTRY);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.SITENAME);
        inputFieldsListOptional.add(InputFields.SUPPORT_URL);
        inputFieldsListOptional.add(InputFields.PARTNER_ID);
        inputFieldsListOptional.add(InputFields.AGENTID);
        inputFieldsListOptional.add(InputFields.ACTION);
        inputFieldsListOptional.add(InputFields.NOTIFY_EMAIL);


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
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
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
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.AGENTID);
        inputFieldsListMandatory.add(InputFields.ACTION);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
