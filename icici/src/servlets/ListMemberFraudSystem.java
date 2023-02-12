import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
import servlets.ChargesUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
 * User: sandip
 * Date: 9/9/14
 * Time: 4:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListMemberFraudSystem extends HttpServlet
{
    private static Logger logger = new Logger(ListMemberFraudSystem.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in ListMemberFraudSystem");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("ctoken===" + req.getParameter("ctoken"));

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        ServletContext application = getServletContext();
        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberFraudSystem.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        String memberId = req.getParameter("memberid");
        String fsid = req.getParameter("fsid");
        int start = 0; // start index
        int end = 0; // end index

        //Functions fn=new Functions();
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",5,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select id,fsid,memberid,isActive,isonlinefraudcheck,isapiuser,isTest from member_fraudsystem_mapping where id>0");
            StringBuffer countquery = new StringBuffer("select count(*) from member_fraudsystem_mapping where id>0");

            if (functions.isValueNull(memberId))
            {
                query.append(" and memberid='" + ESAPI.encoder().encodeForSQL(me,memberId ) + "'");
                countquery.append(" and memberid='" + ESAPI.encoder().encodeForSQL(me,memberId) + "'");
            }
            if (functions.isValueNull(fsid))
            {
                query.append(" and fsid='" + ESAPI.encoder().encodeForSQL(me,fsid ) + "'");
                countquery.append(" and fsid='" + ESAPI.encoder().encodeForSQL(me,fsid) + "'");
            }
            query.append(" order by id desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);
            hash = ChargesUtils.getHashFromResultSet(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            logger.debug("forward to jsp" + hash);
        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/listMemberFraudSystem.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.FSID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
