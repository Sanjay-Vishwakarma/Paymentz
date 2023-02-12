import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import com.manager.vo.PaginationVO;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 Created by IntelliJ IDEA.
 User: sandip
 Date: 9/14/2015
 Time: 6:24 PM
 To change this template use File | Settings | File Templates.
 */
public class ViewPartnerBankAccountsDetails extends HttpServlet
{
    private static Logger log = new Logger(ViewPartnerBankAccountsDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        int records=15;
        int pageno=1;

        Functions functions = new Functions();
        Hashtable hash = null;

        PaginationVO paginationVO = new PaginationVO();
        String partnerId = req.getParameter("partnerid");

        RequestDispatcher rd = req.getRequestDispatcher("/partnerBankAccountsDetails.jsp?ctoken="+user.getCSRFToken());

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select accountid,isActive from gateway_account_partner_mapping where partnerid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from gateway_account_partner_mapping where partnerid>0");
            if (functions.isValueNull(partnerId))
            {
                query.append(" and partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerId));
                countquery.append(" and partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerId));
            }
            query.append(" order by partnerid LIMIT " + start + "," + end);
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSetForTransactionEntry(rs);

            ps=conn.prepareStatement(countquery.toString());
            rs = ps.executeQuery();
            rs.next();

            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            req.setAttribute("partnerid",partnerId);
            rd.forward(req, res);
        }
        catch (SystemError s)
        {
            log.error("SystemError while getting partner bank accounts details",s);
            req.setAttribute("partnerid", partnerId);
            req.setAttribute("message","SystemError while getting partner bank accounts details");
            rd.forward(req, res);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception while getting partner bank accounts details",e);
            req.setAttribute("partnerid", partnerId);
            req.setAttribute("message","SQL Exception while getting partner bank accounts details");
            rd.forward(req, res);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
}
