import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

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
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 3/31/13
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoActionHistory extends HttpServlet
{
    Connection cn = null;
    private static Logger logger = new Logger(MyMonederoActionHistory.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in ActionHistory");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        int records=15;
        int pageno=1;
        Hashtable hash = null;
        String error="";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            error+="<font color=red>Invalid Parameters</font><br>";
            logger.error("Enter valid input",e);
        }
        String toid = req.getParameter("toid");
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
        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        
        String query="select T.trackingid as TrackingId,T.accountid,T.currency,D.amount,D.action,D.status,D.responsetransactionid as wctxnid,D.timestamp,M.wcredirecturl,M.sourceid,M.destid from transaction_common as T,transaction_common_details as D,transaction_mymonedero_details as M where D.detailid=M.detailid and T.trackingid=D.trackingid";
        if (functions.isValueNull(fdtstamp))
        {
            query+=" and T.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp);
        }

        if (functions.isValueNull(tdtstamp))
        {
            query+=" and T.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp);

        }

        if (functions.isValueNull(toid))
        {
            query+=" and toid=" + ESAPI.encoder().encodeForSQL(me,toid);
        }
        if (functions.isValueNull(trackingid))
        {
            query+=" and D.trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid);
        }

        String count="select count(*) from ( " + query + ") as temp ";
        query+=" order by D.trackingid desc LIMIT " + start + "," + end;

        ResultSet rs = null;

        try
        {
            //logger.debug("QUERY IS ------------"+query);
            cn= Database.getConnection();
            Hashtable transactionHistory=null;
            transactionHistory = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            rs = Database.executeQuery(count.toString(), cn);


            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));

            
            //logger.debug(transactionHistory);
            req.setAttribute("transactionHistory",transactionHistory);

        }
        catch (SQLException se)
        {
            logger.error("SQL EXCEPTION OCCURED",se);
            error+="<font color=red>Error Occured while Accessing Database</font><br>";
        }
        catch (SystemError se)
        {
            logger.error("SYSTEM ERROR OCCURED",se);
            error+="<font color=red>Error Occured while Accessing Database</font><br>";
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        req.setAttribute("error",error);
        req.getRequestDispatcher("/myMonederoActionHistory.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
