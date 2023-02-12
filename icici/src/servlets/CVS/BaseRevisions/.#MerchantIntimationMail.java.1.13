//package servlets;

import com.directi.pg.*;
import com.payment.Mail.MailService;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.String;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/9/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 * */
public class MerchantIntimationMail extends HttpServlet
{
    private static Logger log = new Logger(MerchantIntimationMail.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
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
        String errormsg = "";
        String EOL = "<BR>";
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/merchantintimationmail.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        Functions functions = new Functions();
        String mailSubject = null;
        int mailTemplateId = 0;
        String toid="";
        String accountId= "";
        String mailevent = req.getParameter("mailevent");

        if(!req.getParameter("toid").equals("0"))
        {
            toid = req.getParameter("toid");
        }
        if(!req.getParameter("accountid").equals("0"))
        {
            accountId = req.getParameter("accountid");
        }

        if(!functions.isValueNull(toid)&&!functions.isValueNull(mailevent))
        {
            StringBuffer str = new StringBuffer("Invalid MemberID & Please Select Mail Event");
            req.setAttribute("sErrorMsg",str.toString());
            RequestDispatcher redi = req.getRequestDispatcher("/merchantintimationmail.jsp?ctoken="+user.getCSRFToken());
            redi.forward(req, res);
            return;


        }

        if(!functions.isValueNull(toid))

        {

                StringBuffer sErrorMsg = new StringBuffer("Invalid MemberID");
                req.setAttribute("sErrorMsg", sErrorMsg.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/merchantintimationmail.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;

        }



        if(!functions.isValueNull(mailevent))
        {
            StringBuffer sErrorMsg = new StringBuffer("Please Select Mail Event");
            req.setAttribute("sErrorMsg",sErrorMsg.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/merchantintimationmail.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }



        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            conn= Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT DISTINCT ma.memberid,m.contact_emails FROM member_account_mapping AS ma, members AS m WHERE ma.memberid=m.memberid AND isActive='Y' AND isTest='N'");
            if (functions.isValueNull(toid))
            {
                query.append(" and ma.memberid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and ma.accountid='" + ESAPI.encoder().encodeForSQL(me,accountId) + "'");
            }

            log.debug("Query:-"+query);

            StringBuffer subjectQry = new StringBuffer("select mailSubject,mailTemplateId from mappingmailtemplateevententity as map,mailevent as me where me.mailEventId=map.mailEventId and me.mailEventName=?");
            //StringBuffer subjectQry=new StringBuffer("SELECT mailSubject,m.mailEventId FROM mappingmailtemplateevententity AS map,mailevent AS m WHERE m.mailEventId=map.mailEventId AND mailTemplateId=? AND m.mailEventName=?");
            ps = conn.prepareStatement(subjectQry.toString());
            ps.setString(1, mailevent);
            rs = ps.executeQuery();
            if(rs.next())
            {
                mailSubject = rs.getString("mailSubject");
                mailTemplateId = rs.getInt("mailTemplateId");
            }
            log.debug("query for mail----"+ps);
            MailService mailService = new MailService();
            String template=mailService.getMailTemplate(mailTemplateId);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            req.setAttribute("toid",toid);
            req.setAttribute("accountid",accountId);
            req.setAttribute("template",template);
            req.setAttribute("mailSubject",mailSubject);
            req.setAttribute("transdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/SendMail.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {
            log.error("error while fetch record from database",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL error while fetch record from database",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        /*inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
*/
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID);



        inputValidator.InputValidations(req, inputFieldsListMandatory, true);
    }
}
