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
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 8/8/16
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ISOCommissionWireList extends HttpServlet
{
    private static Logger log = new Logger(ISOCommissionWireList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        Hashtable hash = null;

        String accountId=req.getParameter("accountid");
        String isPaid=req.getParameter("ispaid");

        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        int records=15;
        int pageno=1;

        String errormsg="";
        String EOL = "<BR>";

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/isocommwiremanagerlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        String fdtstamp=Functions.converttomillisec(fmonth,fdate,fyear,"0","0","0");
        String tdtstamp=Functions.converttomillisec(tmonth,tdate,tyear,"23","59","59");

        start = (pageno - 1) * records;
        end = records;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        ResultSet rs = null;
        try
        {
            Functions functions = new Functions();
            conn= Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT iso_comm_id,accountid,startdate,enddate,settleddate,amount,netfinalamount,unpaidamount,currency,status,reportfilepath,transactionfilepath,from_unixtime(creationdate) as wirecreationtime from iso_commission_wire_manager where iso_comm_id>0 ");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM iso_commission_wire_manager where iso_comm_id>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and creationdate >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and creationdate >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and creationdate <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and creationdate <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(accountId))
            {
                query.append(" and accountid='" + ESAPI.encoder().encodeForSQL(me,accountId) + "'");
                countquery.append(" and accountid='" + ESAPI.encoder().encodeForSQL(me,accountId) + "'");
            }
            if (functions.isValueNull(isPaid))
            {
                if(isPaid.equalsIgnoreCase("Y"))
                {
                    query.append(" and status='paid'");
                    countquery.append(" and status='paid'");
                }
                else
                {
                    query.append(" and status='unpaid'");
                    countquery.append(" and status='unpaid'");
                }
            }
            query.append(" ORDER BY creationdate DESC limit "+start + "," +end);

            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            int totalrecords=0;
            if(rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            req.setAttribute("transdetails", hash);
        }
        catch (SystemError se)
        {
            log.error("SystemError:::::::"+se);
            //req.setAttribute("message", "Internal error while processing your request");
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::::" + e);
            //req.setAttribute("message","Internal error while processing your request");
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/isocommwiremanagerlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req,res);
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