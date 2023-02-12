import com.directi.pg.*;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/17/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberDetailList extends HttpServlet
{
    private static Logger log = new Logger(MemberDetailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index

        String memberId = null;
        String contactName=null;
        String companyName=null;
        String emailId=null;
        String userName=null;

        errorList = validateOptionalParameters(req);
        Hashtable memberHash = null;

        if(errorList!=null && !errorList.equals(""))
        {
            //redirect to jsp page for invalid data entry
            req.setAttribute("error",errorList);
            RequestDispatcher rd = req.getRequestDispatcher("/memberlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            memberId=req.getParameter("memberid");
            companyName=req.getParameter("company_name");
            contactName=req.getParameter("contact_persons");
            emailId=req.getParameter("contact_emails");
            userName=req.getParameter("username");
            req.setAttribute("memberid",memberId);
            req.setAttribute("company_name",companyName);
            req.setAttribute("contact_persons",contactName);
            req.setAttribute("contact_emails",emailId);
            req.setAttribute("username",userName);
        }
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        try
        {
            start = (pageno - 1) * records;
            end = records;
            conn= Database.getConnection();
            StringBuilder selectMemberDetail=new StringBuilder("SELECT * FROM members WHERE memberid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from members WHERE memberid>0");
            if(memberId!=null && !memberId.equals(""))
            {
                selectMemberDetail.append(" and memberid="+memberId);
                countquery.append(" and memberid="+memberId);
            }
            if(userName!=null && !userName.equals(""))
            {
                selectMemberDetail.append(" and login='"+userName+"'");
                countquery.append(" and login='"+userName+"'");
            }
            if(companyName!=null && !companyName.equals(""))
            {
                selectMemberDetail.append(" and company_name='"+companyName+"'");
                countquery.append(" and company_name='"+companyName+"'");
            }
            if(contactName!=null && !contactName.equals(""))
            {
                selectMemberDetail.append(" and contact_persons='"+contactName+"'");
                countquery.append(" and contact_persons='"+contactName+"'");
            }
            if(emailId!=null && !emailId.equals(""))
            {
                selectMemberDetail.append(" and contact_emails='"+emailId+"'");
                countquery.append(" and contact_emails='"+emailId+"'");
            }
            selectMemberDetail.append(" order by memberid desc LIMIT " + start + "," + end);
            memberHash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(selectMemberDetail.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            memberHash.put("totalrecords", "" + totalrecords);
            memberHash.put("records", "0");

            if (totalrecords > 0)
                memberHash.put("records", "" + (memberHash.size() - 2));

            req.setAttribute("transdetails", memberHash);
            //redirect to jsp page with records

            RequestDispatcher rd = req.getRequestDispatcher("/memberlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError while listing member details",systemError);
        }
        catch (SQLException e)
        {
            log.error("Sql exception while listing member details", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.COMPANY_NAME);
        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);
        inputFieldsListOptional.add(InputFields.USERNAME);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

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
