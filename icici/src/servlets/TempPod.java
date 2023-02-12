import com.directi.pg.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Hashtable;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

public class TempPod extends HttpServlet
{   private static Logger logger = new Logger(TempPod.class.getName());


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in TempPod");
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;
        int pageno=1;
        int records=15;
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();


        String data = req.getParameter("data");

        //Functions fn=new Functions();

        try
        {
        pageno =Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true),1);
        records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true),15);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;

         }

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;

        StringBuffer query = new StringBuffer("select T.*,M.company_name from transaction_icicicredit as T,members as M");
        StringBuffer countquery = new StringBuffer("select count(*) from transaction_icicicredit as T,members as M");

        query.append(" where T.status='capturestarted' and M.memberid=T.toid order by T.authid LIMIT " + start + "," + end);
        countquery.append(" where T.status='capturestarted' and M.memberid=T.toid");

        try
        {
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), Database.getConnection()));
            ResultSet rs = Database.executeQuery(countquery.toString(), Database.getConnection());
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            req.setAttribute("poddetails", hash);

            /*RequestDispatcher rd = req.getRequestDispatcher("/temppod.jsp");
            rd.forward(req, res);*/
        }
        catch (SystemError se)
        {   logger.error("System Error in TempPod",se);

            sErrorMessage.append("Internal Error Records not Found \r\n");
        }
        catch (Exception e)
        {   logger.error("Exception in TrmpPod",e);
            sErrorMessage.append("Internal Error Records not Found \r\n");
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/temppod.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);

    }
}
