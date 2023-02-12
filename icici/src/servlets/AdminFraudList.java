
import com.directi.pg.*;

import javax.servlet.RequestDispatcher;
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
import java.util.Hashtable;
import java.util.List;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

public class AdminFraudList extends HttpServlet
{

    private static Logger logger = new Logger(AdminFraudList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");
        String description=null;
        String trakingid=null;
        String errormsg="";
        int pageno=1;
        int records=15;
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input ::::::",e);
            errormsg = errormsg + "Please Enter valid value in following field.";
        }
        description = req.getParameter("SDescription");
        trakingid = req.getParameter("STrakingid");

        if(errormsg!="")
        {
            logger.debug("ENTER VALID DATA");
            req.setAttribute("error",errormsg);
        }
        //Functions fn=new Functions();
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

        //	out.println("merchantid "+ merchantid);

        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select F.*,M.company_name from members as M,fraud_transaction_list as F where F.icicitransid>0");
        StringBuffer countquery = new StringBuffer("select count(*) from members as M,fraud_transaction_list as F where F.icicitransid>0");

        if (functions.isValueNull(description))
        {
            query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,description) + "%'");
            countquery.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,description) + "%'");
        }

        if (functions.isValueNull(trakingid))
        {
            query.append(" and F.icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
            countquery.append(" and F.icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
        }


        query.append(" and F.processed='N' and F.toid=M.memberid  order by F.icicitransid desc LIMIT " + start + "," + end);
        countquery.append(" and F.processed='N' and F.toid=M.memberid");

        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            //	out.println(hash.toString());

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            //	out.println(hash.toString());
            req.setAttribute("transdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/adminfraudlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("System Error::::::",se);

        }
        catch (Exception e)
        {   logger.error("Exception::::::::",e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.SDESCRIPTION);
        inputFieldsListMandatory.add(InputFields.STRACKINGID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
