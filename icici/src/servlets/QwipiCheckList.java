import com.directi.pg.Admin;
import com.directi.pg.*;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiCheckList extends HttpServlet
{
    private static Logger log = new Logger(QwipiCheckList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in QwipiCheckList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";
        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        ServletContext application = getServletContext();
            Connection conn = null;
            Functions functions = new Functions();
            int records=15;
            int pageno=1;
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
            RequestDispatcher rd = req.getRequestDispatcher("/qwipichecklist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String toid= req.getParameter("toid");
        String mid= req.getParameter("fromid");
        String status = req.getParameter("status");
        String trackingid = req.getParameter("trackingid");
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
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        req.setAttribute("toid",toid);
        req.setAttribute("fromid",mid);
        req.setAttribute("status",status);
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
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("select trackingid,toid,description,amount,refundamount,status,timestamp,fromid,qwipiPaymentOrderNumber,accountid from transaction_qwipi where trackingid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_qwipi where trackingid>0");

            if (functions.isValueNull(toid))/*(toid != null && !toid.equals("null") && !toid.equals(""))*/
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                countquery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(trackingid))/*(trackingid != null && !trackingid.equals("null") && !trackingid.equals(""))*/
            {
                query.append(" and trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                countquery.append(" and trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }

            if (functions.isValueNull(status))/*(status != null && !status.equals("null") && !status.equals(""))*/
            {
                query.append(" and status='" + status + "'");
                countquery.append(" and status='" + status + "'");
            }

            if (functions.isValueNull(mid))/*(mid != null && !mid.equals("null") && !mid.equals(""))*/
            {
                query.append(" and fromid='" + ESAPI.encoder().encodeForSQL(me,mid) + "'");
                countquery.append(" and fromid='" + ESAPI.encoder().encodeForSQL(me,mid) + "'");
            }

            if (functions.isValueNull(fdtstamp))/*(fdtstamp != null && !fdtstamp.equals("null") && !fdtstamp.equals("")*/
            {
                query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (functions.isValueNull(tdtstamp))/*(tdtstamp != null && !tdtstamp.equals("null") && !tdtstamp.equals(""))*/
            {
                query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            query.append(" order by trackingid desc LIMIT " + start + "," + end);


            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
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
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/qwipichecklist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.FROMID);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_TRA);
        inputFieldsListMandatory.add(InputFields.STATUS_LIST);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
