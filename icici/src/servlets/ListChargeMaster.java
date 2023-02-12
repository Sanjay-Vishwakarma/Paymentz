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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListChargeMaster extends HttpServlet
{
    static Logger log = new Logger(ListChargeMaster.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in List Charge Master");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            log.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/listChargeMaster.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        String chargeId = req.getParameter("chargeid");
        String chargeName = req.getParameter("chargename");
        String isInputRequired = req.getParameter("isinputrequired");



        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select chargeid,chargename,isinputrequired,keyname,valuetype,category,keyword,subkeyword,frequency,sequencenum,actionExecutorId,actionExecutorName from charge_master where chargeid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from charge_master where chargeid>0");


            if (functions.isValueNull(chargeId))
            {
                query.append(" and chargeid='" + ESAPI.encoder().encodeForSQL(me,chargeId) + "'");
                countquery.append(" and chargeid='" + ESAPI.encoder().encodeForSQL(me,chargeId) + "'");
            }
            if (functions.isValueNull(chargeName))
            {
                query.append(" and chargename LIKE '%" + ESAPI.encoder().encodeForSQL(me,chargeName) + "%'");
                countquery.append(" and chargename LIKE'%" + ESAPI.encoder().encodeForSQL(me,chargeName) + "%'");
            }

            if (functions.isValueNull(isInputRequired))
            {
                query.append(" and isinputrequired='" + ESAPI.encoder().encodeForSQL(me,isInputRequired) + "'");
                countquery.append(" and isinputrequired='" + ESAPI.encoder().encodeForSQL(me,isInputRequired) + "'");
            }
           query.append(" order by chargeid desc LIMIT " + start + "," + end);

            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);

        }
        catch (SystemError s)
        {
            log.error("System error while perform select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/listChargeMaster.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.CHARGEID);
        inputFieldsListMandatory.add(InputFields.CHARGENAME);
        inputFieldsListMandatory.add(InputFields.ISINPUTREQ);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
