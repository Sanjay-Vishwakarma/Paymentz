import com.directi.pg.*;

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
import java.util.Hashtable;
import java.util.List;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

public class AdminProofrequiredList extends HttpServlet
{

    private static Logger logger = new Logger(AdminProofrequiredList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in AdminProofrequiredList");
        ServletContext ctx = getServletContext();
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;
        String firstfourofccnum=null;
        String lastfourofccnum=null;
        String trakingid=null;
        res.setContentType("text/html");
        String errormsg="";
        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");
        String cc = Functions.checkStringNull(req.getParameter("SCc"));
        int pageno=1;
        int records=15;

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
         logger.error("Invalid input",e);

            sErrorMessage.append("Please Enter valid value in TrakingID and CreditCard number.");
        }
        firstfourofccnum = req.getParameter("firstfourofccnum");
        lastfourofccnum = req.getParameter("lastfourofccnum");
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
        StringBuffer query = new StringBuffer("select T.status,T.icicitransid,T.authid,T.description,concat(b.first_six,'******',b.last_four) as \"ccnum\",T.amount,M.company_name from transaction_icicicredit as T, members as M, bin_details as b  where T.icicitransid>0");
        StringBuffer countquery = new StringBuffer("select count(*) from transaction_icicicredit as T, members as M, bin_details as b  where T.icicitransid>0");


        if (functions.isValueNull(firstfourofccnum))
        {
            query.append(" and b.first_six = '" + ESAPI.encoder().encodeForSQL(me,firstfourofccnum) + "'");
            countquery.append(" and b.first_six ='" + ESAPI.encoder().encodeForSQL(me,firstfourofccnum) + "'");
        }
        if (functions.isValueNull(lastfourofccnum))
        {
            query.append(" and b.last_four = '" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum) + "'");
            countquery.append(" and b.last_four ='" + ESAPI.encoder().encodeForSQL(me,lastfourofccnum) + "'");
        }
        if (functions.isValueNull(trakingid))
        {
            query.append(" and T.icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
            countquery.append(" and T.icicitransid=" + ESAPI.encoder().encodeForSQL(me,trakingid));
        }


        query.append(" and status='proofrequired' and T.toid=M.memberid  and T.icicitransid = b.icicitransid order by T.icicitransid desc LIMIT " + start + "," + end);
        countquery.append(" and status='proofrequired' and T.toid=M.memberid and T.icicitransid = b.icicitransid");

        
        
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();

            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            ctx.log("Hash in Admin Proof req. list " + hash.toString());
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
            firstfourofccnum=null;
            lastfourofccnum=null;
            hash=null;
            /*RequestDispatcher rd = req.getRequestDispatcher("/adminprooflist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);*/
        }
        catch (SystemError se)
        {   logger.error("System error::: in AdminProofrequiredList",se);

             sErrorMessage.append("Internal System Error");
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   logger.error("Exception :::: in AdminProofrequiredList",e);
            sErrorMessage.append("Internal System Error");

        }

        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        logger.debug("leaving AdminProofrequiredList");


        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/adminprooflist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.FIRSTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.STRACKINGID);
        inputFieldsListMandatory.add(InputFields.LASTFOURCCNUM);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
