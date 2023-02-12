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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class AdminChargebackReverseList extends HttpServlet
{
    private static Logger logger = new Logger(AdminChargebackReverseList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        logger.debug("Entering in AdminChargebackReverseList ");
        int start = 0; // start index
        int end = 0; // end index

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        res.setContentType("text/html");
         int pageno = 1;
        int records = 15;

        PrintWriter out = res.getWriter();
        String description=null;
        String trakingid=null;
        String captureid=null;
        String errormsg = "";
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
          logger.error("Invalid input",e);
          errormsg = errormsg + "Please Enter valid value in following field.";
        }
        description = req.getParameter("SDescription");
        trakingid = req.getParameter("STrakingid");
        captureid = req.getParameter("SCaptureid");
        if(errormsg!="")
        {   logger.debug("ENTER VALID DATA");
            req.setAttribute("error",errormsg);
        }

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

        logger.debug("Enter in to select query for fatch Chargeback Transection");
        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select T.status,T.icicitransid,T.transid,T.captureid,T.description,T.captureamount-T.refundamount as amount,M.company_name from transaction_icicicredit as T,members as M where status='chargeback'");
        StringBuffer countquery = new StringBuffer("select count(*) from transaction_icicicredit as T,members as M where status='chargeback'");

        if (functions.isValueNull(description))
        {
            query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,description) + "%'");
            countquery.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,description) + "%'");
        }

        if (functions.isValueNull(trakingid))
        {
            query.append(" and icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
            countquery.append(" and icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
        }

        if (functions.isValueNull(captureid))
        {
            logger.debug("====CaptureId: "+captureid);
            query.append(" and captureid='" + ESAPI.encoder().encodeForSQL(me,captureid)+"'");
            countquery.append(" and captureid='" + ESAPI.encoder().encodeForSQL(me,captureid)+"'");
        }

        query.append("  and T.toid=M.memberid  order by T.icicitransid desc LIMIT " + start + "," + end);
        countquery.append(" and T.toid=M.memberid");

        Connection conn = null;
        ResultSet rs=null;
        try
        {
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
        }
        catch (SystemError se)
        {   logger.error("SystemError occure",se);
             sErrorMessage.append("Record not found <br> \r\n");
        }
        catch (Exception e)
        {   logger.error("Exception:::::::",e);
            sErrorMessage.append("Record not found <br> \r\n");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());

        StringBuilder chargeBackResult = new StringBuilder();
        chargeBackResult.append(req.getAttribute("cbreversemessage"));
        req.setAttribute("cbreversemessage", chargeBackResult.toString());
        String redirectpage = "/adminchargebackreverselist.jsp?ctoken="+user.getCSRFToken();
        
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.SDESCRIPTION);
        inputFieldsListMandatory.add(InputFields.STRACKINGID);
        inputFieldsListMandatory.add(InputFields.SCAPTUREID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
