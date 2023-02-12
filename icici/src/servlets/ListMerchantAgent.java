import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
import servlets.ChargesUtils;

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
 * User: sandip1
 * Date: 6/29/15
 * Time: 4:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListMerchantAgent extends HttpServlet
{
    private static Logger logger = new Logger(ListMerchantAgent.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session)){
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        Hashtable hash = null;
        RequestDispatcher rd = req.getRequestDispatcher("/listMerchantAgent.jsp?ctoken="+user.getCSRFToken());
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            rd.forward(req, res);
        }
        String memberId = req.getParameter("memberid");
        String agentId = req.getParameter("agentid");

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records", e);
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
            StringBuffer query = new StringBuffer("select mam.mappingid,mam.memberid,mam.agentid,a.agentName as agentname,m.company_name as merchantname,from_unixtime(creationtime) as mappingon from merchant_agent_mapping as mam join agents as a on mam.agentid=a.agentid join members as m on mam.memberid=m.memberid ");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_agent_mapping as mam join agents as a on mam.agentid=a.agentid join members as m on mam.memberid=m.memberid ");
            if (functions.isValueNull(memberId))
            {
                query.append(" and mam.memberid='" + ESAPI.encoder().encodeForSQL(me,memberId ) + "'");
                countquery.append(" and mam.memberid='" + ESAPI.encoder().encodeForSQL(me,memberId) + "'");
            }
            if (functions.isValueNull(agentId))
            {
                query.append(" and mam.agentid='" + ESAPI.encoder().encodeForSQL(me,agentId ) + "'");
                countquery.append(" and mam.agentid='" + ESAPI.encoder().encodeForSQL(me,agentId) + "'");
            }
            query.append(" order by mappingid desc LIMIT " + start + "," + end);

            hash = ChargesUtils.getHashFromResultSet(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
        }
        catch (SystemError s)
        {
            //errormsg += ""+ errormsg + s.getMessage() + EOL + "";
            logger.error(s.getMessage());
            /*req.setAttribute("errormessage", errormsg);
            rd.forward(req,res);*/
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            //errormsg += ""+ errormsg + e.getMessage() + EOL + "";
            logger.error(e.getMessage());
            /*req.setAttribute("errormessage", errormsg);
            rd.forward(req,res);*/
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        req.setAttribute("errormessage", errormsg);
        rd = req.getRequestDispatcher("/listMerchantAgent.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req,res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.AGENT_ID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
