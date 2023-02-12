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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.*;



/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/8/12
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiDetailList extends HttpServlet
{
    Connection cn = null;
    private static Logger logger = new Logger(QwipiDetailList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in QwipiDetailList");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");


        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection con=null;
        String errormsg = "";
        String EOL = "<BR>";
        boolean flag = true;
        String status=null;

        res.setContentType("text/html");
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Invalid input",e);
            flag= false;
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            logger.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/qwipidetaillist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

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
        ServletContext application = getServletContext();
        int start = 0; // start index
        int end = 0;
        Hashtable transactionHistory = null;

        int pageno=1;
        int records=30;
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
        try
        {
            con= Database.getConnection();
            //StringBuffer query=new StringBuffer("select t.parentid,t.action,t.status,t.timestamp,t.amount,t.responseDateTime,t.qwipiPaymentOrderNumber,t.responseRemark,q.description,t.ipaddress from transaction_qwipi_details as t,transaction_qwipi as q where t.parentid>0 and t.parentid=q.trackingid");
            StringBuffer query=new StringBuffer("select t.parentid,t.action,t.status,t.timestamp,t.amount,t.responseDateTime,t.qwipiPaymentOrderNumber,t.responseRemark,q.description,t.actionexecutorid,t.actionexecutorname,t.ipaddress from transaction_qwipi_details as t,transaction_qwipi as q where t.parentid>0 and t.parentid=q.trackingid");
            StringBuffer count=new StringBuffer("select count(*) from transaction_qwipi_details as t,transaction_qwipi as q where t.parentid>0 and t.parentid=q.trackingid");

            if(isValueNull(trackingid))
            {
                query.append(" and t.parentid="+ESAPI.encoder().encodeForSQL(me,trackingid));
                count.append(" and t.parentid="+ESAPI.encoder().encodeForSQL(me,trackingid));
            }
            if (fdtstamp != null)
            {
                //query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                //count.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (tdtstamp != null)
            {
                //query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                //count.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }


            query.append(" order by parentid desc LIMIT " + start + "," + end);

            logger.debug("Query:-"+query.toString());
            logger.debug("CountQuery:-"+count.toString());
            transactionHistory = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));

            ResultSet rs = Database.executeQuery(count.toString(), con);
            rs.next();
            int totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));
            req.setAttribute("transactionHistory", transactionHistory);
            logger.debug("forward to jsp"+transactionHistory);

        }
        catch(Exception e)
        {
            logger.error("ERROR",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/qwipidetaillist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }
}
