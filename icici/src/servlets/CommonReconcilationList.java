import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
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
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/28/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonReconcilationList extends HttpServlet
{
    private static Logger log = new Logger(CommonReconcilationList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in CommonReconcilationList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        Connection conn = null;
        ResultSet rs = null;
        String error= "";

        int records=15;
        int pageno=1;

        error = error + validateMandatoryParameters(req);
        String accountid= req.getParameter("accountid");
        req.setAttribute("accountid",accountid);

        error = error + validateOptionalParameters(req);
        String paymentid= req.getParameter("paymentid");
        req.setAttribute("paymentid",paymentid);

        String trackingid= req.getParameter("trackingid");
        req.setAttribute("trackingid",trackingid);

        String desc= req.getParameter("description");
        req.setAttribute("description",desc);

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
        }
        String gateway = req.getParameter("gateway");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        req.setAttribute("trackingid", trackingid);
        req.setAttribute("paymentnumber", paymentid);
        req.setAttribute("description", desc);

        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        int start = 0; // start index
        int end = 0; // end index
        Hashtable hash = null;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select trackingid,toid,description,fromid,amount,status,timestamp,accountid,paymentid from transaction_common where status IN('authstarted','markedforreversal')");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_common where status IN('authstarted','markedforreversal')");

            if (functions.isValueNull(trackingid))
            {
                query.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
                countquery.append(" and trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
            }
            if (functions.isValueNull(accountid))
            {
                query.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
                countquery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
            }
            if (functions.isValueNull(paymentid))
            {
                query.append(" and paymentid=" + ESAPI.encoder().encodeForSQL(me,paymentid));
                countquery.append(" and paymentid=" + ESAPI.encoder().encodeForSQL(me,paymentid));
            }
            if (functions.isValueNull(desc))
            {
                query.append(" and description=" + ESAPI.encoder().encodeForSQL(me,desc));
                countquery.append(" and description=" + ESAPI.encoder().encodeForSQL(me,desc));
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            query.append(" order by trackingid desc LIMIT " + start + "," + end);

            log.debug("==="+query);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            //req.setAttribute("transdetails", hash);

        }
        catch(SystemError s)
        {
            log.error("SystemError",s);

        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
            error= error+"Sql Exception while listing records.<br>";
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        if(req.getParameter("cbmessage")!=null &&!req.getParameter("cbmessage").equals(""))
        {
            req.setAttribute("cbmessage",req.getParameter("cbmessage"));
        }
        else if (error!=null && !error.equals(""))
        {
            req.setAttribute("error",error);
        }
        else {
            req.setAttribute("transdetails", hash);
        }
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/commonReconcilation.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);

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
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.DESCRIPTION);

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
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
