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
import java.sql.Statement;
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
public class ChargebackReport extends HttpServlet
{
    Connection cn = null;
    private static Logger logger = new Logger(ChargebackReport.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        ServletContext application = getServletContext();
        String searchstatus="";
        String searchType="";
        String searchId=null;
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

        searchType = req.getParameter("searchtype");
        searchId = req.getParameter("SearchId");

        logger.debug("type"+searchType);
        logger.debug("ID"+searchId);



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

        fdate = req.getParameter("fdate");
        tdate = req.getParameter("tdate");
        fmonth = req.getParameter("fmonth");
        tmonth = req.getParameter("tmonth");
        fyear = req.getParameter("fyear");
        tyear = req.getParameter("tyear");

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
        //date filter end
    /*if(req.getParameter("SearchId")==null || req.getParameter("SearchId").equals(""))
    {
       errormsg = errormsg + "Please Enter Value for search "+EOL;
        flag = false;

    }
    if(req.getParameter("searchtype")==null || req.getParameter("searchtype").equals(""))
    {
       errormsg = errormsg + "Please select any one serchType"+EOL;
        flag = false;
    }*/

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
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;

        if(flag==false)
        {
            logger.debug(errormsg);
            req.setAttribute("error",errormsg);
            logger.debug(errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/chargebackReport.jsp");
            rd.forward(req, res);
            return;
        }
        else
        {
            Hashtable isoCodeMap = new   Hashtable();
            ResultSet rs = null;
            try
            {
                cn = Database.getConnection();
                Statement stmnt = cn.createStatement();
                String query = "select code,reason from rs_codes";
                rs = stmnt.executeQuery(query);

                isoCodeMap = Functions.getDetailedHashFromResultSet(rs);
            }
            catch (Exception e)
            {
                logger.error("Error while loading Exceptional Transaction",e);
            }
            finally
            {

                Database.closeResultSet(rs);
                Database.closeConnection(cn);
            }
        /*if(searchType.equals("Toid") || searchType.equals("mid"))
        {*/
            StringBuffer query = new StringBuffer("select C.icicitransid, C.description, C.amount,C.processed, C.merchantid,T.status, C.toid,C.processed,C.last_update_date,C.fk_chargeback_process_id,date_format(STR_TO_DATE(C.cb_date,'%d%m%Y'),'%d/%m/%Y') as cb_date ,C.cb_reason,IF(C.cb_partial IS NULL,'N',C.cb_partial) as cb_partial,C.cb_indicator from chargeback_transaction_list as C, transaction_icicicredit as T  where C.processed='Y' and C.icicitransid=T.icicitransid");
            StringBuffer count = new StringBuffer("select count(*) from chargeback_transaction_list as C, transaction_icicicredit as T where C.processed='Y' and C.icicitransid=T.icicitransid");

            if(searchType!=null && searchType.equals("Toid") )
            {
                query.append("  and C.toid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and C.toid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if( searchType!=null && searchType.equals("mid") )
            {
                query.append("  and C.merchantid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and C.merchantid='"+ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if (fdtstamp != null)
            {
                query.append(" and UNIX_TIMESTAMP(STR_TO_DATE(C.cb_date,'%d%m%Y')) >= " + fdtstamp);
                count.append(" and UNIX_TIMESTAMP(STR_TO_DATE(C.cb_date,'%d%m%Y')) >= " + fdtstamp);
            }
            if (tdtstamp != null)
            {
                query.append(" and UNIX_TIMESTAMP(STR_TO_DATE(C.cb_date,'%d%m%Y')) <= " + tdtstamp);
                count.append(" and UNIX_TIMESTAMP(STR_TO_DATE(C.cb_date,'%d%m%Y')) <= " + tdtstamp);
            }
            query.append(" order by merchantid DESC limit " + start + "," + end);

            try
            {
                cn = Database.getConnection();
                hash = Database.getHashFromResultSetForExceptionalTrans(Database.executeQuery(query.toString(),cn),isoCodeMap);

                logger.debug("record"+hash);
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
                RequestDispatcher rd = req.getRequestDispatcher("/chargebackReport.jsp?ctoken="+user.getCSRFToken());
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
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.SEARCHTYPE_SMALL);
        inputFieldsListMandatory.add(InputFields.SEARCH_ID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}



