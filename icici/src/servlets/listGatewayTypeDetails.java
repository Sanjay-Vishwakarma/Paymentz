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
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 3/2/14
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class listGatewayTypeDetails extends HttpServlet
{
    static Logger log = new Logger(listGatewayTypeDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in listGatewayTypeDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;
        Functions functions = new Functions();
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            req.setAttribute("message",e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/gatewayInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String pgtypeid = req.getParameter("pgtypeid");
        String gateway = req.getParameter("gateway");

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
            StringBuffer query = new StringBuffer("select pgtypeid,gateway,currency,name,templatename,bank_emailid from gateway_type where pgtypeid > 0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from gateway_type where pgtypeid > 0 ");


            if (functions.isValueNull(pgtypeid))
            {
                query.append(" and pgtypeid =" + ESAPI.encoder().encodeForSQL(me,pgtypeid));
                countquery.append(" and pgtypeid =" + ESAPI.encoder().encodeForSQL(me,pgtypeid));
            }
            if (functions.isValueNull(gateway))
            {
                query.append(" and gateway = '"  +(gateway)+ "'");
                countquery.append(" and gateway ='" + (gateway) + "'");
            }


            query.append(" order by pgtypeid desc LIMIT " + start + "," + end);


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
            log.error("System error while performing select query",s);
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
        RequestDispatcher rd = req.getRequestDispatcher("/gatewayInterface.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.PGTYPEID);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.PAGENO);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
