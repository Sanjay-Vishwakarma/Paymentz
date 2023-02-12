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
 * User: Jitendra
 * Date: 27/02/18
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListExchangeRates extends HttpServlet
{
    static Logger log = new Logger(ListExchangeRates.class.getName());

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
            RequestDispatcher rd = req.getRequestDispatcher("/listExchangeRates.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String fromCurrency = req.getParameter("fromcurrency");
        String toCurrency = req.getParameter("tocurrency");


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
            StringBuffer query = new StringBuffer("select id,from_currency,to_currency,exchange_rate,from_unixtime(dtstamp)as dtstamp,timestamp from currency_exchange_rates where id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from currency_exchange_rates where id>0");


            if (functions.isValueNull(fromCurrency))
            {
                query.append(" and from_currency='" + ESAPI.encoder().encodeForSQL(me,fromCurrency) + "'");
                countquery.append(" and from_currency='" + ESAPI.encoder().encodeForSQL(me,fromCurrency) + "'");
            }
            if (functions.isValueNull(toCurrency))
            {
                query.append(" and to_currency ='" + ESAPI.encoder().encodeForSQL(me,toCurrency) + "'");
                countquery.append(" and to_currency ='" + ESAPI.encoder().encodeForSQL(me,toCurrency) + "'");
            }
            query.append(" order by id desc LIMIT " + start + "," + end);

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
        RequestDispatcher rd = req.getRequestDispatcher("/listExchangeRates.jsp?ctoken="+user.getCSRFToken());
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
