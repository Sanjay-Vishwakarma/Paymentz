import com.directi.pg.*;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 17/7/15.
 */
public class FraudSystemAccountRuleList extends HttpServlet
{
    private static Logger logger = new Logger(FraudSystemAccountRuleList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String fsaccountid = null;
        String status = null;
        String value=null;
        Functions functions = new Functions();
        StringBuffer sb=new StringBuffer();

        Hashtable hash = null;
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            logger.debug("message..."+e.getMessage());
            request.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemAccountRuleList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

        fsaccountid = request.getParameter("fsaccountid");
        status = request.getParameter("status");
        value= request.getParameter("value");

        //RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemAccountRuleList.jsp?ctoken="+user.getCSRFToken());


        if (!ESAPI.validator().isValidInput("status",status,"SafeString",255,true))
        {
            sb.append("Invalid status,");
        }
        if (!ESAPI.validator().isValidInput("fsaccountid",fsaccountid,"Numbers",11,true))
        {
            sb.append("Invalid Fraud System Account,");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg",sb.toString());
            RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemAccountRuleList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index


        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {

            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select rm.ruleid,rm.rulename,arm.status,arm.score,arm.value,arm.fsaccountid,fam.accountname from account_rule_mapping as arm join rule_master as rm on arm.ruleid=rm.ruleid\n" +
                    "join fraudsystem_account_mapping as fam on arm.fsaccountid=fam.fsaccountid  ");
            StringBuffer countquery = new StringBuffer("select count(*) from account_rule_mapping as arm join rule_master as rm on arm.ruleid=rm.ruleid ");

            if(functions.isValueNull(fsaccountid))
            {
                query.append(" and arm.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsaccountid));
                countquery.append(" and arm.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsaccountid));
            }
            if(functions.isValueNull(status))
            {
                query.append(" and arm.status='"+status+"'");
                countquery.append(" and arm.status='"+status+"'");
            }
            query.append(" LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);
            logger.debug("forward to jsp" + hash);

        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            //sb.append("System error while perform select query" + s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            //sb.append("SQL Exception while perform select query" + e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("statusMsg",sb.toString());
        RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemAccountRuleList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
        return;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }


}
