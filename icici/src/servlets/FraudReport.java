import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jun 29, 2012
 * Time: 4:57:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudReport extends HttpServlet
{
    Connection cn = null;
    static Logger logger = new Logger(FraudReport.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        ServletContext application = getServletContext();
        String searchstatus="";
        String errormsg = "";
        String EOL = "<BR>";
        boolean flag = true;
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Input valid value",e);
            errormsg = errormsg + "Please Enter valid value "+EOL;
        }
        String searchType = req.getParameter("searchtype");
        String searchId = req.getParameter("SearchId");

        int start = 0; // start index
        int end = 0; // end index

        //Add Date Filter start
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invelid description",e);
        }
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth =  req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        searchId=ESAPI.encoder().encodeForSQL(me,searchId);

        logger.debug("Entering in Chargeback Report");
        PrintWriter out = res.getWriter();
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
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;
        if(flag==false)
        {
            logger.debug(errormsg);
            req.setAttribute("error",errormsg);
            logger.debug(errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/fraudReport.jsp");
            rd.forward(req, res);
            return;
        }
        else
        {
            StringBuffer query = new StringBuffer("select F.icicitransid,T.status,T.name,T.emailaddr, F.description, F.amount,F.toid,date_format(F.last_update_date,'%d/%m/%Y') as \"last_update_date\",F.merchantid from fraud_transaction_list as F, transaction_icicicredit as T where F.icicitransid=T.icicitransid");
            StringBuffer count = new StringBuffer("select count(*) from fraud_transaction_list as F,transaction_icicicredit as T  where F.icicitransid=T.icicitransid");
            if(searchType!=null && searchType.equals("Toid") )
            {
                query.append("  and F.toid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and F.toid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if( searchType!=null && searchType.equals("mid") )
            {
                query.append("  and F.merchantid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and F.merchantid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and UNIX_TIMESTAMP(F.last_update_date) >= " + fdtstamp);
                count.append(" and UNIX_TIMESTAMP(F.last_update_date) >= " + fdtstamp);
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and UNIX_TIMESTAMP(F.last_update_date) <= " + tdtstamp);
                count.append(" and UNIX_TIMESTAMP(F.last_update_date) <= " + tdtstamp);
            }
            query.append(" order by merchantid DESC limit " + start + "," + end);
            ResultSet rs = null;

            try
            {
                cn = Database.getConnection();
                hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(),cn));

                rs = Database.executeQuery(count.toString(), cn);
                rs.next();
                int totalrecords = rs.getInt(1);
                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");

                if (totalrecords > 0)
                    hash.put("records", "" + (hash.size() - 2));

                req.setAttribute("report", hash);
                if(req.getParameter("SearchId")==null || req.getParameter("SearchId").equals(""))
                {
                    req.setAttribute("SearchId",searchId);
                }
                if(req.getParameter("searchtype")==null || req.getParameter("searchtype").equals(""))
                {
                    req.setAttribute("searchtype",searchType);
                }
                RequestDispatcher rd = req.getRequestDispatcher("/fraudReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (Exception e)
            {   logger.error("SQL",e);
                out.println(Functions.ShowMessage("Error!", e.toString()));
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closeConnection(cn);
            }
        }
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.SEARCHTYPE_SMALL);
        inputFieldsListMandatory.add(InputFields.SEARCH_ID);
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



